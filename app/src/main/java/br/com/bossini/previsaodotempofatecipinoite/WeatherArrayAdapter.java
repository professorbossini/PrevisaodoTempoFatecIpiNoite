package br.com.bossini.previsaodotempofatecipinoite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rodrigo on 11/10/17.
 */

public class WeatherArrayAdapter extends ArrayAdapter <Weather>{

    private Map<String, Bitmap> bitmaps = new HashMap<>();
    private static class ViewHolder{
        ImageView conditionImageView;
        TextView lowTextView;
        TextView highTextView;
        TextView humidityTextView;
        TextView dayTextView;
    }
    public WeatherArrayAdapter (Context context, List<Weather> data){
       super(context, -1, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather day = getItem (position);
        Context context = getContext();
        ViewHolder viewHolder;
        if (convertView == null){//primeira vez
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.conditionImageView = (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.highTextView = (TextView) convertView.findViewById(R.id.highTextView);
            viewHolder.humidityTextView = (TextView) convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.highTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.   humidity));

        if (bitmaps.containsKey(day.iconURL)){
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        }
        else{
            new LoadImageTask (viewHolder.conditionImageView).execute(day.iconURL);
            //Picasso.with(context).load(day.iconURL).into(viewHolder.conditionImageView);
        }

        /*LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.list_item, parent, false);
        ImageView conditionImageView = linearLayout.findViewById(R.id.conditionImageView);
        TextView dayTextView = linearLayout.findViewById(R.id.dayTextView);
        TextView lowTextView = linearLayout.findViewById(R.id.lowTextView);
        TextView highTextView = linearLayout.findViewById(R.id.highTextView);
        TextView humidityTextView = linearLayout.findViewById(R.id.humidityTextView);
        dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        highTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        humidityTextView.setText(context.getString(R.string.humidity, day.humidity));*/
        //return linearLayout;
        return convertView;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        public LoadImageTask (ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                try(InputStream inputStream = connection.getInputStream ()){
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put (params[0], bitmap);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally{
                connection.disconnect();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}