/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.peter.arfood.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.peter.arfood.R;
import com.example.peter.arfood.RestClient;
import com.example.peter.arfood.models.Explore;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ExploreFragment extends AbsListViewBaseFragment {
    public static final int INDEX = 1;
    private Context context;
    List<String> exploreImages = new ArrayList<>();
    RestClient restClient = RestClient.getInstance();
    boolean isDownloadDone = false;
    public static String[] imageArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        final GridView listView = (GridView) rootView.findViewById(R.id.grid);

        RestClient.ResultReadyCallback callback = new RestClient.ResultReadyCallback() {
            @Override
            public void resultReady(List<Explore> explores) {
                for(Explore explore: explores) {
                    exploreImages.add(explore.image);

                }
                Log.d("explore: ", String.valueOf(exploreImages));
                isDownloadDone = true;
                imageArray = new String[exploreImages.size()];
                imageArray = imageListTOArray(exploreImages);
                listView.setAdapter(new ImageAdapter(getActivity()));
            }
        };
        restClient.setCallback(callback);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position", String.valueOf(position));
            }

        });
        return rootView;
    }



    private class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private DisplayImageOptions options;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return imageArray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageLoader.getInstance()
                    .displayImage(imageArray[position], holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return view;
        }
    }

    public String[] imageListTOArray(List<String> exploreImages) {
       String[] imageArray = new String[exploreImages.size()];

        for (int i = 0; i<exploreImages.size();i++) {
            imageArray[i] = "https://food-s14785236952.c9users.io/restaurant_image/"+exploreImages.get(i)+".jpg";
        }
        return imageArray;
    }
    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    @Override
    public void onResume() {
        super.onResume();
        restClient.getExplores();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}