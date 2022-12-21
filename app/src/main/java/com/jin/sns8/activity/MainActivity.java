package com.jin.sns8.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jin.sns8.R;
import com.jin.sns8.fragment.HomeFragment;
import com.jin.sns8.fragment.UserInfoFragment;
import com.jin.sns8.fragment.UserListFragment;

import java.io.File;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private long backBtnTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle(getResources().getString(R.string.app_name));

        init();
        checkRooting();

    }
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this,"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init();
                break;
        }
    }

    private void init(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            myStartActivity(com.jin.sns8.activity.SignUpActivity.class);
        } else {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(com.jin.sns8.activity.MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, homeFragment)
                    .commit();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            HomeFragment homeFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, homeFragment)
                                    .commit();
                            return true;
                        case R.id.myInfo:
                            UserInfoFragment userInfoFragment = new UserInfoFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, userInfoFragment)
                                    .commit();
                            return true;
                        case R.id.userList:
                            UserListFragment userListFragment = new UserListFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, userListFragment)
                                    .commit();
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    protected boolean checkRooting(){
        final String[] files = {
                "/sbin/su", "system/su", "system/bin/su", "system/sbin/su", "system/xbin/su",
                "/system/.ext/.su", "system/bin/.ext", "su", "busybox", "supersu", "Superuser.apk",
                "KingoUser.apk", "SuperSu.apk","magisk"};
        String buildTags = Build.TAGS;

        if (buildTags != null && buildTags.contains("test-keys")){
            return true;
        }

        for (String path : files){
            if(new File(path).exists()){
                return true;
            }
        }

        try {
            Runtime.getRuntime().exec("su");
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}