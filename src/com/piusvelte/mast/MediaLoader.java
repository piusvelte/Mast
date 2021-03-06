/*
 * Mast - Cast Web Media Player
 * Copyright (C) 2013 Bryan Emmanuel
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Bryan Emmanuel piusvelte@gmail.com
 */
package com.piusvelte.mast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.piusvelte.mast.utils.MediaUrlUtils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class MediaLoader extends AsyncTaskLoader<List<Medium>> {

    private static final String TAG = MediaLoader.class.getSimpleName();
    private static final String MEDIA_LIBRARY_URL_FORMAT = "%s/mast.py";

    List<Medium> media = null;
    URL mediaUrl = null;

    public MediaLoader(Context context, String host) {
        super(context);
        setUrl(host);
    }

    public void loadHost(String host) {
        if (setUrl(host)) forceLoad();
    }

    private boolean setUrl(String host) {
        mediaUrl = MediaUrlUtils.getUrl(String.format(MEDIA_LIBRARY_URL_FORMAT, host));
        return mediaUrl != null;
    }

    @Override
    public List<Medium> loadInBackground() {
        List<Medium> media = new ArrayList<Medium>();
        if (mediaUrl != null) {
            HttpURLConnection httpURLConnection;

            try {
                String response;
                httpURLConnection = (HttpURLConnection) mediaUrl
                        .openConnection();
                InputStream in = new BufferedInputStream(
                        httpURLConnection.getInputStream());
                byte[] buffer = new byte[512];
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                int readBytes = 0;
                while ((readBytes = in.read(buffer)) != -1) {
                    content.write(buffer, 0, readBytes);
                }

                response = new String(content.toByteArray());
                JSONParser jsonParser = new JSONParser();
                JSONArray mediaJArr = (JSONArray) jsonParser.parse(response);
                Gson gson = new Gson();

                for (int i = 0, s = mediaJArr.size(); i < s; i++) {
                    media.add(gson.fromJson(mediaJArr.get(i).toString(),
                            Medium.class));
                }
            } catch (IOException e) {
                Log.e(TAG, "Error loading media", e);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing media", e);
            }
        }
        return media;
    }

    @Override
    public void deliverResult(List<Medium> media) {
        this.media = media;

        if (isStarted()) {
            super.deliverResult(media);
        }
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged() || (media == null)) {
            forceLoad();
        } else if (media != null) {
            deliverResult(media);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        media = null;
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
