package com.example.kmp.Notification;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.media.session.MediaButtonReceiver;

import com.example.kmp.Modeles.Musique;
import com.example.kmp.Activity.PlayingMusicActivity;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;

public class PlayingNotificationImpl extends PlayingNotification{

    private boolean isPlaying = true;

    @Override
    public void update() {
        stopped = false;

        final Musique musique = service.getCurrentSong();

        isPlaying = service.isPlaying();


        Intent action = new Intent(service, PlayingMusicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);
        stackBuilder.addNextIntentWithParentStack(action);
        final PendingIntent clicIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(musique.getTitreMusique())
                .setContentText(musique.getNomArtiste() + " * " +musique.getTitreAlbum())
                .setLargeIcon(buildCircleBitmap(musique.getPochette(),service.getResources(),0))
                .setContentIntent(clicIntent)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(service,PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.logo)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0,1,2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(service,PlaybackStateCompat.ACTION_STOP)))
                .setShowWhen(false);

        addActionsOnNotifications(builder);

        updateNotifyModeAndPostNotification(builder.build());

    }

    private void addActionsOnNotifications(NotificationCompat.Builder builder) {
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                isPlaying?R.drawable.ic_pause_black_24dp:R.drawable.ic_play_arrow_black_24dp,
                isPlaying?service.getString(R.string.play):service.getString(R.string.pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
        );

        NotificationCompat.Action prevAction = new NotificationCompat.Action(
                R.drawable.ic_skip_previous_black_24dp,
                service.getString(R.string.precedent),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )

        );

        NotificationCompat.Action nextAction = new NotificationCompat.Action(
                R.drawable.ic_skip_next_black_24dp,
                service.getString(R.string.suivant),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                )

        );

        builder
                .addAction(prevAction)
                .addAction(playPauseAction)
                .addAction(nextAction);
    }

    private void linkButtons(RemoteViews notificationLayout, RemoteViews notificationBigContent){
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(service,PlayerService.class);
        pendingIntent = buildPendingIntent(service, PlayerService.ACTION_SKIP_TO_PREVIOUS, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_prev, pendingIntent);
        notificationBigContent.setOnClickPendingIntent(R.id.action_prev, pendingIntent);

        pendingIntent = buildPendingIntent(service, PlayerService.ACTION_TOGGLE_PAUSE, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);
        notificationBigContent.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);

        pendingIntent = buildPendingIntent(service, PlayerService.ACTION_SKIP_TO_NEXT, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_next, pendingIntent);
        notificationBigContent.setOnClickPendingIntent(R.id.action_next, pendingIntent);
    }

    private PendingIntent buildPendingIntent(Context context, final String action, final ComponentName componentName){
        Intent intent = new Intent(action);
        intent.setComponent(componentName);
        return PendingIntent.getService(context,0, intent,0);
    }

    private static Bitmap createBitmap(Drawable drawable, float sizeMultiplier){
        Bitmap bitmap = Bitmap.createBitmap((int)(drawable.getIntrinsicWidth() * sizeMultiplier) ,(int)(drawable.getIntrinsicHeight() * sizeMultiplier),Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0,0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return bitmap;
    }

    private Bitmap buildCircleBitmap(String imagePath, Resources res, int size){
        Bitmap bitmap = null;
        if(imagePath!=null){
            bitmap = BitmapFactory.decodeFile(imagePath);
            if(bitmap==null){
                bitmap = BitmapFactory.decodeResource(service.getResources(),R.drawable.logo);
            }
        }else{
            bitmap = BitmapFactory.decodeResource(service.getResources(),R.drawable.logo);
        }

        return bitmap;

        /*Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = res.getColor(android.R.color.white);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawOval(rectF,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,rect, rect, paint);
        bitmap.recycle();*/
    }


}
