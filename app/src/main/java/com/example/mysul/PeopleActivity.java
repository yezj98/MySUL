package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mysul.Interface.FirebaseLoadDone;
import com.example.mysul.Interface.IRecyclerItemClickListener;
import com.example.mysul.Model.MyResponse;
import com.example.mysul.Model.Request;
import com.example.mysul.Model.User;
import com.example.mysul.Remote.IFCMservice;
import com.example.mysul.utils.Common;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PeopleActivity extends AppCompatActivity implements FirebaseLoadDone {



    //Adapter process data
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recyclerView;
    FirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar materialSearchBar;
    List<String> suggestList = new ArrayList<>();

    IFCMservice ifcMservice;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        getSupportActionBar().hide();

        //initial the API
        ifcMservice = Common.getFCMservice();

        materialSearchBar = findViewById(R.id.search_bar);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            //Add the listener for synchronize the search bar by the information that chosen by user
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                    materialSearchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //Add an action listener for search bar
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    if (adapter != null) {
                        //if close search, restore default
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                starSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recyclerView = findViewById(R.id.recyleAll);
        recyclerView.setHasFixedSize(true);

        //Layout manager set the style of scroll
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PeopleActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        //Decoration draw a divider between option and option
        recyclerView.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        firebaseLoadDone = this;
        loadUserList();
        loadSearchData();
    }


    // User list
    private void loadUserList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull final User user) {
                if (user.getEmail().equals(Common.loggeduser.getEmail())) {
                    userViewHolder.userEmail.setText(new StringBuilder(user.getEmail()).append("(me)"));
                    userViewHolder.userEmail.setTypeface(userViewHolder.userEmail.getTypeface(), Typeface.ITALIC);
                } else {
                    userViewHolder.userEmail.setText(new StringBuilder(user.getEmail()));
                }

                //Event
                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogRequest(user);
                    }
                });
            }

            @NonNull
            @Override
            //Create view holder and set the data
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Using inflater to load user layout under the search bar
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user, parent, false);
                return new UserViewHolder(itemView);
            }
        };
        //avoid all blank in load user
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void loadSearchData() {
        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    User user = userSnapShot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.FirebaseLoadUserName(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.FirebaseFailure(databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (adapter != null) {
            adapter.startListening();
        }
        if (searchAdapter != null) {
            searchAdapter.startListening();
        }
        super.onResume();
    }

    private void starSearch(String textSearch) {
        Query query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION).orderByChild("email").startAt(textSearch); // Search the
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull User user) {
                if (user.getEmail().equals(Common.loggeduser.getEmail())) {
                    userViewHolder.userEmail.setText(new StringBuilder(user.getEmail()).append("(me)"));
                    userViewHolder.userEmail.setTypeface(userViewHolder.userEmail.getTypeface(), Typeface.ITALIC);
                } else {
                    userViewHolder.userEmail.setText(new StringBuilder(user.getEmail()));
                }

                //Event
                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {

                    }
                });

            }

            @NonNull
            @Override
            //Create view holder and set the data
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user, parent, false);
                return new UserViewHolder(itemView);
            }
        };
        //avoid all blank in load user
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    @Override
    public void FirebaseLoadUserName(List<String> lstName) {
        materialSearchBar.setLastSuggestions(lstName);
    }

    @Override
    public void FirebaseFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDialogRequest (final User model){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this,R.style.MyRequestDialog);
        alertdialog.setTitle("Request Friend");
        alertdialog.setMessage("Do you want to send the friend request" +" " + model.getEmail());
        alertdialog.setIcon(R.drawable.ic_man);
        //Set the "NO" button
        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertdialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add to accept list
                //Take the data
                DatabaseReference acceptList= FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
                        .child(Common.USER_INFORMATION)
                        .child(Common.loggeduser.getUid())
                        .child(Common.ACCEPT_LIST);

                acceptList.orderByKey().equalTo(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){ //If the user is not inside the friendlist
                            sendFriendRequest(model);
                        }
                        else
                            Toast.makeText(PeopleActivity.this, "You and" +model.getEmail()+ "already are friends", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        alertdialog.show();
    }



    private void sendFriendRequest(final User model) {


        //Get the token to sent
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);
        Log.d ("token", "" + tokens);

        tokens.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){
                    Toast.makeText(PeopleActivity.this, "Token Error!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //create a request
                    Request request = new Request();

                    //create data
                    Map<String, String > datasend = new HashMap<>();
                    datasend.put(Common.FROM_UID, Common.loggeduser.getUid());
                    datasend.put(Common.FROM_NAME, Common.loggeduser.getEmail());
                    datasend.put(Common.TO_UID, model.getUid());
                    datasend.put(Common.TO_NAME, model.getEmail());

                    request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));

                    request.setData(datasend);

                    Log.d ("request1", ""+ request);


                    //send
                    compositeDisposable.add(ifcMservice.sendFriendRequestToUser(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<MyResponse>() {
                        @Override
                        public void accept(MyResponse myResponse) throws Exception {
                            Log.d ("responsee", "" + myResponse);
                            if (myResponse.success == 1) {
                                Toast.makeText(PeopleActivity.this, "Request Sent!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(PeopleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
