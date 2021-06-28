


        package com.example.project_layouts;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.project_layouts.Database;
import com.example.project_layouts.FriendsRecommendation;
import com.example.project_layouts.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;


public class ClassificationService extends Service {

    private final IBinder binder = new LocalBinder();

    private Socket s = null;
    private BufferedReader in=null;
    private PrintWriter pw = null;

    private Socket sSpotify = null;
    private BufferedReader inSpotify=null;
    private PrintWriter pwSpotify = null;
    String IP = "5.29.118.39";

    ArrayList<String> arrayFavsUser = null;
    ArrayList<Integer> arrayFavsValuesUser;
    String userName;
    DatabaseReference usersRef;
    FirebaseAuth auth;

    int recFriendsCounter = 0;
    Intent notiIntent;
    Context notiContext;
    Database database;
    DatabaseReference reference;
    PendingIntent notiPendingIntent;
    NotificationCompat.Builder notiBuilder;
    NotificationManagerCompat notificationManager;

    private final int START_SERVICE_ID = 5;

    private RunOnThread runOnThread = null;
    String [] songsURLsArray;//spotifyAPI
    String [] songsAlbumsNamesArray;//spotifyAPI


    public class LocalBinder extends Binder {
        ClassificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ClassificationService.this;
        }
    }

    private Notification sendNotification(String message, int id){
        if(id == START_SERVICE_ID)
        {
            notiBuilder = new NotificationCompat.Builder(this.notiContext, "myNotify")
                    .setAutoCancel(true);
            return notiBuilder.build();
        }

        notiBuilder = new NotificationCompat.Builder(this.notiContext, "myNotify")
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("MCP")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(notiPendingIntent);

        Notification notification = notiBuilder.build();
        notificationManager.notify(id, notification);
        return notification;
    }

    public void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotifyChannel";
            String description = "Channel for my notify";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myNotify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            startForeground(1, sendNotification("on start foreground", START_SERVICE_ID));
        }
    }

    @Override
    public void onCreate() {
        this.notiContext = ClassificationService.this;
        this.notiIntent = new Intent(notiContext, FriendsRecommendation.class);
        this.notiPendingIntent = PendingIntent.getActivity(notiContext, 1,
                notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.notificationManager = NotificationManagerCompat.from(notiContext);
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        userName = FirebaseAuth.getInstance().getUid();


        if(this.runOnThread == null){
            RunOnThread friendsRecommend = new RunOnThread("friendsRecommend");
            new Thread(friendsRecommend).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


    public void stopOwnService(){
        stopSelf();
    }

    public String [] getSongsURLsArray(){
        return this.songsURLsArray;
    }

    public String [] getSongsAlbumsNamesArray(){
        return this.songsAlbumsNamesArray;
    }


    public void sendSongToClassify(String filePath){
        RunOnThread classifySong = new RunOnThread(filePath);
        new Thread(classifySong).start();

    }

    private class RunOnThread implements Runnable
    {
        String filePath = "";

        RunOnThread(String filePath){
            this.filePath = filePath;
        }

        @Override
        public void run() {
            if(filePath.equals("friendsRecommend")) {
                //while (true) {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                friendsRecommend();
                requestSpotifyAPI();
                //}
            }
            else{
                classifySong();
            }
        }


        private void friendsRecommend(){
            recFriendsCounter = 0;
            arrayFavsUser = new ArrayList<>();
            arrayFavsValuesUser = new ArrayList<>();
            usersRef = FirebaseDatabase.getInstance().getReference().child("users");

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> recNames = new ArrayList<>();
                    int sumUserFaves=0;

                    for (DataSnapshot userFav : dataSnapshot.child(userName).child("favorites").getChildren()) {
                        arrayFavsUser.add(userFav.getKey());
                        arrayFavsValuesUser.add(Integer.parseInt(userFav.getValue().toString()));
                        sumUserFaves+= Integer.parseInt(userFav.getValue().toString());
                    }
                    if(sumUserFaves == 0)
                        return;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.getKey().equals(userName))
                            continue;

                        Users userDB = new Users();
                        userDB.setId(ds.getKey());
                        try{
                            userDB.setName(ds.child("name").getValue().toString());
                        }catch (Exception exception){
                            continue;
                        }

                        DataSnapshot favoritesRef = ds.child("favorites");
                        int sumDataBaseUserFaves=0;

                        for (DataSnapshot dataBaseFav : favoritesRef.getChildren()) {
                            int faveValue = Integer.parseInt(dataBaseFav.getValue().toString());
                            sumDataBaseUserFaves+= faveValue;
                            if(faveValue > 0)
                                userDB.addFaivorite(dataBaseFav.getKey());
                        }
                        if(sumDataBaseUserFaves == 0)
                            continue;

                        int index;

                        for (DataSnapshot fav : favoritesRef.getChildren()) {
                            if((index=arrayFavsUser.indexOf(fav.getKey())) >= 0) {
                                int value = Integer.parseInt(fav.getValue().toString());
                                double comparePercentage; /*(user's # of songs of genre /
                           user's # of all the songs) - (databaseUser's # of songs of same genre /
                           databaseUser's # of all the songs)*/

                                double comparePartition; /*(user's # of songs of genre -
                           databaseUser's # of songs of same genre) / (user's # of songs of genre +
                           databaseUser's # of songs of same genre)*/

                                comparePercentage = Math.abs(((double) arrayFavsValuesUser.get(index))/
                                        ((double)sumUserFaves) - (double)value/((double)sumDataBaseUserFaves));

                                comparePartition = Math.abs(((double)(arrayFavsValuesUser.get(index) - value)) /
                                        ((double)(arrayFavsValuesUser.get(index) + value)));

                                if(comparePercentage < 0.8 || comparePartition < 0.8) {

                                    Database database = new Database((getApplicationContext()));
                                    ;
                                    database.putRecommendation(userDB.getId(), userDB.getName(), userDB.printGeneres());
                                    recFriendsCounter++;
                                    break;
                                }
                            }
                        }
                    }

                    if(recFriendsCounter > 0){
                        String friend = "friends!";
                        if(recFriendsCounter == 1)
                            friend = "friend!";
                        sendNotification("You have " + recFriendsCounter +
                                " recommended " + friend, 300);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            usersRef.addListenerForSingleValueEvent(eventListener);
        }



        private void requestSpotifyAPI(){
            try {



                Database database = new Database(getApplicationContext());
                int [] counters = database.getCountersForAllGenres();
                String [] genres = {"Blues", "Classical", "Country", "Disco", "Hiphop", "Jazz", "Metal", "Pop", "Reggae", "Rock"};
                String favesStr="";
                int genresCounter = 0;
                for(int i=0 ; i< genres.length ; i++)
                    if(counters[i] > 0){
                        favesStr+=genres[i] + String.valueOf(counters[i]) + ';';
                        genresCounter++;
                    }
                if(genresCounter == 0)
                    return;

                sSpotify = new Socket(IP, 1234);
                pwSpotify = new PrintWriter(sSpotify.getOutputStream());
                inSpotify = new BufferedReader(new InputStreamReader(sSpotify.getInputStream()));

                String serverAction = "spotify";
                pwSpotify.write(serverAction);
                pwSpotify.flush();
                String sizeFormatof2chars = "%2d";
                sizeFormatof2chars = String.format(sizeFormatof2chars, genresCounter);
                pwSpotify.write(sizeFormatof2chars);
                pwSpotify.flush();

                //String str = "rock1;pop5;classical8;";
                String sizeFormatof10chars = "%4d";
                sizeFormatof10chars = String.format(sizeFormatof10chars, favesStr.length());
                pwSpotify.write(sizeFormatof10chars);
                pwSpotify.flush();
                pwSpotify.write(favesStr);
                pwSpotify.flush();

                ReadResultFromServer readResultFromServer = new ReadResultFromServer();
                new Thread(readResultFromServer).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void classifySong(){
            File wavFile=null;
            FileInputStream fileInputStream;
            int size=5;
            byte[] bytes = null;

            try{
                wavFile = new File(filePath);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            try {
                size = (int) wavFile.length();

                bytes = new byte[size];
                fileInputStream = new FileInputStream(wavFile);
                fileInputStream.read(bytes);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {

                s = new Socket(IP, 1234);
                pw = new PrintWriter(s.getOutputStream());
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                String serverAction = "classify";
                pw.write(serverAction);
                pw.flush();

                auth = FirebaseAuth.getInstance();
                String userEmail = auth.getCurrentUser().getEmail();
                String splitEmail[] = userEmail.split("@");
                String userName = String.format("%-20s", splitEmail[0]);
                pw.write(userName);
                pw.flush();

                String sizeFormatof10chars = "%10d";
                sizeFormatof10chars = String.format(sizeFormatof10chars, size);
                pw.write(sizeFormatof10chars);
                pw.flush();

                s.getOutputStream().write(bytes);

                ReadResultFromServer readResultFromServer = new ReadResultFromServer(filePath, userName);
                new Thread(readResultFromServer).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class ReadResultFromServer implements Runnable{

        String filePath;
        String userName;

        ReadResultFromServer(String filePath, String userName){
            this.filePath = filePath;
            this.userName = userName;
        }

        ReadResultFromServer(){
            this.filePath = null;
            this.userName = null;
        }

        @Override
        public void run() {
            if(filePath != null && userName != null){
                String messageRec = "";
                database = new Database(getApplicationContext());
                try {
                    int charsRead = 0;
                    char[] buffer = new char[9];//longest genre is 'Classical' with 9 bytes

                    while ((charsRead = in.read(buffer)) != -1)  {
                        messageRec += new String(buffer).substring(0, charsRead);
                        database.updateGenere(filePath, messageRec);
                        reference = FirebaseDatabase.getInstance().getReference("users").child(userName).child("favorites").child(messageRec);
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int counter_of_genre = 0;

                                //update counter of song genere
                                try {
                                    counter_of_genre = Integer.parseInt(dataSnapshot.getValue().toString());
                                    counter_of_genre +=1;
                                }catch (Exception e){
                                    counter_of_genre =1;

                                }

                                reference.setValue(String.valueOf(counter_of_genre));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        reference.addListenerForSingleValueEvent(eventListener);

                        in.reset();
                        in.close();
                        pw.close();
                        s.close();
                    }
                } catch (IOException e) {
                }
            }

            else{//spotifyAPI
                String msgLength = "";
                String msg="";
                try {
                    int charsRead = 0;
                    char[] msgLengthBuffer = new char[4];//longest genre is 'Classical' with 9 bytes
                    char[] msgBuffer;

                    if ((charsRead = inSpotify.read(msgLengthBuffer)) != -1) {
                        msgLength += new String(msgLengthBuffer).substring(0, charsRead);
                    }
                    else
                        return;

                    msgLength = msgLength.trim();
                    msgBuffer = new char[Integer.valueOf(msgLength)];

                    if ((charsRead = inSpotify.read(msgBuffer)) != -1) {
                        msg += new String(msgBuffer).substring(0, charsRead);
                    }

                    songsURLsArray = msg.split(";");

                    msg="";
                    msgLength="";
                    msgLengthBuffer = new char[4];
                    if ((charsRead = inSpotify.read(msgLengthBuffer)) != -1) {
                        msgLength += new String(msgLengthBuffer).substring(0, charsRead);
                    }
                    else
                        return;

                    msgLength = msgLength.trim();
                    msgBuffer = new char[Integer.valueOf(msgLength)];

                    if ((charsRead = inSpotify.read(msgBuffer)) != -1) {
                        msg += new String(msgBuffer).substring(0, charsRead);
                    }

                    songsAlbumsNamesArray = msg.split(";");

                    inSpotify.reset();
                    inSpotify.close();
                    pwSpotify.flush();
                    pwSpotify.close();
                    sSpotify.close();
                } catch (IOException e) {
                }
            }
        }
    }


}