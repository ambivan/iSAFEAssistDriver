package com.prateek.isafeassistdriver;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.prateek.isafeassistdriver.dao.NotifyDao;

public class MyMessagingService extends FirebaseMessagingService {
    DatabaseReference reference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    NotifyDao notifyDao = new NotifyDao();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        reference = FirebaseDatabase.getInstance().getReference();

        //if(auth!=null){
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());


    }

    public void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notify").setContentTitle(title)
                .setSmallIcon(R.mipmap.driver).setAutoCancel(true).setContentText(message);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
        notifyDao.setTitle(title);
        notifyDao.setBody(message);
        System.out.println("Notifications " + notifyDao);

        reference.child("Notifications").push().setValue(notifyDao, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {
                    System.out.println("Success");

                }else{
                    System.out.println("Failure");
                }
            }
        });


    }
}
