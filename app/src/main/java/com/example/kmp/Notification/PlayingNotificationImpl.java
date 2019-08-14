package com.example.kmp.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Activity.PlayingMusicActivity;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;

import static android.view.View.INVISIBLE;

public class PlayingNotificationImpl extends PlayingNotification{

    private boolean isPlaying = true;

    @Override
    public void update() {
        stopped = false;

        final Musique musique = service.getCurrentSong();

        isPlaying = service.isPlaying();

        final RemoteViews notificationLayout = new RemoteViews(service.getPackageName(), R.layout.notification_layout);

        linkButtons(notificationLayout);

        if(TextUtils.isEmpty(musique.getTitreMusique() )&& TextUtils.isEmpty(musique.getNomArtiste())){
            notificationLayout.setViewVisibility(R.id.media_titles, INVISIBLE);
        }else{
            notificationLayout.setViewVisibility(R.id.media_titles, View.VISIBLE);
            notificationLayout.setTextViewText(R.id.title, musique.getTitreMusique());
            notificationLayout.setTextViewText(R.id.text, musique.getNomArtiste());
        }

        Bitmap bitmap = BitmapFactory.decodeFile(musique.getPochette());
        if(bitmap==null)
            bitmap = BitmapFactory.decodeResource(service.getResources(),R.drawable.logo);

        notificationLayout.setImageViewBitmap(R.id.image,buildCircleBitmap(musique.getPochette(),service.getResources(),0));

/*        ImageView imageView = new ImageView(service.getApplicationContext());
        Glide.with(service.getApplicationContext())
                .load(musique.getPochette())
                .asBitmap()
                .error(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .centerCrop()
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(service.getApplicationContext().getResources(),
                                Bitmap.createScaledBitmap(resource,52,52,false));
                        drawable.setCircular(true);
                        notificationLayout.setImageViewBitmap(R.id.image,drawable.getBitmap());
                    }
                });*/



        notificationLayout.setImageViewResource(R.id.action_play_pause, isPlaying?R.drawable.ic_pause_black_24dp:R.drawable.ic_play_arrow_black_24dp);

        Intent action = new Intent(service, PlayingMusicActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent clicIntent = PendingIntent.getActivity(service,0,action,0);
        final PendingIntent deleteIntent = buildPendingIntent(service, PlayerService.ACTION_QUIT, null);
/*
.setSmallIcon(isPlaying?R.drawable.ic_play_arrow_black_24dp:R.drawable.ic_pause_black_24dp)
*/
        final Notification notification = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo,10)
                .setContentIntent(clicIntent)
                .setDeleteIntent(deleteIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(notificationLayout)
                .setOngoing(isPlaying)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .build();

        updateNotifyModeAndPostNotification(notification);

    }

    private RoundedBitmapDrawable getRoundedBitmapDrawable(Musique musique) {
        Bitmap bitmap = BitmapFactory.decodeFile(musique.getPochette());
        if(bitmap==null)
            bitmap = BitmapFactory.decodeResource(service.getResources(),R.drawable.logo);

        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(service.getResources(),
                Bitmap.createScaledBitmap(bitmap,1,1,false));
        drawable.setCircular(true);

        return drawable;
    }

    private void linkButtons(RemoteViews notificationLayout){
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(service,PlayerService.class);
        pendingIntent = buildPendingIntent(service, PlayerService.ACTION_REWIND, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_prev, pendingIntent);

        pendingIntent = buildPendingIntent(service, PlayerService.ACTION_TOGGLE_PAUSE, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);

        pendingIntent = buildPendingIntent(service, PlayerService.ACTION_SKIP, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_next, pendingIntent);
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
        if(!TextUtils.isEmpty(imagePath)){
            bitmap = BitmapFactory.decodeFile(imagePath);
            if(bitmap==null){
                bitmap = BitmapFactory.decodeResource(service.getResources(),R.drawable.logo);
            }
        }else{
            bitmap = BitmapFactory.decodeResource(service.getResources(),R.drawable.logo);
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
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
        bitmap.recycle();

        return output;
    }


}
