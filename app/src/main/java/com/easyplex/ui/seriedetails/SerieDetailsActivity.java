package com.easyplex.ui.seriedetails;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.serie.Season;
import com.easyplex.databinding.SerieDetailsBinding;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.moviedetails.adapters.CastAdapter;
import com.easyplex.util.Constants;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import timber.log.Timber;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.easyplex.util.Constants.SEASONS;
import static com.easyplex.util.Constants.SPECIALS;


/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2020 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/


public class SerieDetailsActivity extends AppCompatActivity {


    public static final String ARG_MOVIE = "movie";


    SerieDetailsBinding binding;

    AdView mAdView;


    @Inject
    SettingsManager settingsManager;

    @Inject ViewModelProvider.Factory viewModelFactory;


    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    AuthManager authManager;


    private com.facebook.ads.AdView facebookBanner;



    private SerieDetailViewModel serieDetailViewModel;



    CastAdapter mCastSerieAdapter;
    EpisodeAdapter mEpisodesSerieAdapter;
    private boolean mSerieLoaded;
    private boolean serieCastLoaded;
    private boolean mEpisodesLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.serie_details);

        mSerieLoaded = false;
        serieCastLoaded = false;
        mEpisodesLoaded = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);

        Intent intent = getIntent();
        Media serie = intent.getParcelableExtra(ARG_MOVIE);

        Tools.hideSystemPlayerUi(this,true,0);

        Tools.setSystemBarTransparent(this);


        // ViewModel to cache, retrieve data for Serie Details
        serieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(SerieDetailViewModel.class);




        if (serie != null){

            serieDetailViewModel.getSerieDetails(serie.getTmdbId());
            serieDetailViewModel.movieDetailMutableLiveData.observe(this, serieDetail -> {
                onLoadImage(serieDetail.getPosterPath());
                onLoadTitle(serieDetail.getName());
                onLoadSeasons(serieDetail.getSeasons(), serieDetail.getTmdbId(),serieDetail.getName());
                onLoadDate(serieDetail.getFirstAirDate());
                onLoadSynopsis(serieDetail.getOverview());
                onLoadAppBar(serieDetail.getName());
                onLoadGenres(serieDetail.getGenres());
                onLoadBackButton();
                onLoadRating(serieDetail.getVoteAverage());
                onLoadFacebookBanner();


                binding.report.setOnClickListener(v -> onLoadReport(serieDetail.getName(), serieDetail.getPosterPath()));
                binding.favoriteIcon.setOnClickListener(view -> {
                    binding.favoriteIcon.toggleWishlisted();
                    onFavoriteClick(serieDetail);
                });



                serieDetailViewModel.isFavorite(Integer.parseInt(serieDetail.getTmdbId())).observe(this, favMovieDetail1 -> {
                    binding.favoriteIcon.setActivated(favMovieDetail1 != null);
                    binding.favoriteIcon.setVisibility(View.VISIBLE);
                });

                binding.ButtonPlayTrailer.setOnClickListener(v -> onLoadTrailer(

                serieDetail.getPreviewPath(), serieDetail.getName(), serieDetail.getBackdropPath()));

                binding.shareIcon.setOnClickListener(v -> onLoadShare(

                        serieDetail.getName(), Integer.parseInt(serieDetail.getTmdbId())

                ));

                    serieDetailViewModel.getSerieCast(Integer.parseInt(serieDetail.getTmdbId()));
                    serieDetailViewModel.serieCreditsMutableLiveData.observe(this, credits -> {


                        if (credits !=null) {


                            mCastSerieAdapter = new CastAdapter(settingsManager);
                            mCastSerieAdapter.addCasts(credits.getCasts());


                            // Starring RecycleView
                            binding.recyclerViewCastMovieDetail.setAdapter(mCastSerieAdapter);
                            binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
                            binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
                            binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(SerieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));

                            serieCastLoaded = true;
                            checkAllDataLoaded();

                        }



                });



                mSerieLoaded = true;
                checkAllDataLoaded();


            });






        }

    }

    private void onLoadFacebookBanner() {

        facebookBanner = new com.facebook.ads.AdView(this, "702141607071810_702619837023987", AdSize.BANNER_HEIGHT_50); // Check Banner Id in Strings
        binding.bannerContainer.addView(facebookBanner);
        facebookBanner.loadAd();
    }

    @SuppressLint("SetTextI18n")
    private void onLoadSeasons(List<Season> seasons, String tmdbId,String tvName) {

        binding.mseason.setText(SEASONS + seasons.size());
        for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
            if(iterator.next().getName().equals(SPECIALS))
                iterator.remove();
        }

        binding.planetsSpinner.setItem(seasons);
        binding.planetsSpinner.setSelection(0);
        binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                Season season = (Season) adapterView.getItemAtPosition(position);
                String episodeId = String.valueOf(season.getId());
                String currentSeason = season.getName();
                String serieId = String.valueOf(tmdbId);
                String seasonNumber = season.getSeasonNumber();

                // Episodes RecycleView
                binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(SerieDetailsActivity.this));
                binding.recyclerViewEpisodes.setHasFixedSize(true);
                mEpisodesSerieAdapter = new EpisodeAdapter(serieId,seasonNumber,episodeId,currentSeason,0,sharedPreferences,authManager,tvName);
                mEpisodesSerieAdapter.addSeasons(season.getEpisodes());
                binding.recyclerViewEpisodes.setAdapter(mEpisodesSerieAdapter);




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // do nothing if no season selected

            }
        });


        mEpisodesLoaded = true;
        checkAllDataLoaded();

    }



    // Animate Appbar for Serie Details
    private void onLoadAppBar(final String title) {

        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {
                if (title != null){

                    binding.collapsingToolbar.setTitle(title);
                    binding.collapsingToolbar.setContentScrimColor(Color.parseColor("#E6070707"));

                }


                binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.planetsSpinner.setVisibility(View.VISIBLE);



            } else {
                binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.INVISIBLE);
                binding.planetsSpinner.setVisibility(View.GONE);
            }
        });


    }



    // Load Serie Rating
    private void onLoadRating(String voteAverage) {

        binding.ratingBar.setRating(Float.parseFloat(voteAverage) / 2);
    }



    // Handle Share Button
    private void onLoadShare(String title, int imdb) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg)+ " " + title + " For more information please check"+("https://www.imdb.com/title/tt" + imdb));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);



    }



    // Add or Remove Serie from Favorite
    public void onFavoriteClick(Media series) {
        if (binding.favoriteIcon.isActivated()) {
            Toast.makeText(this, "Added: " + series.getName(),
                    Toast.LENGTH_SHORT).show();
            serieDetailViewModel.addtvFavorite(series);
        } else {
            Toast.makeText(this, "Removed: " + series.getName(),
                    Toast.LENGTH_SHORT).show();
            serieDetailViewModel.removeTvFromFavorite(series);
        }
    }




    // Handle Back Button
    private void onLoadBackButton() {

        binding.backbutton.setOnClickListener(v -> {
            onBackPressed();
            Animatoo.animateSplit(SerieDetailsActivity.this);

        });
    }


    // Load Serie Trailer
    private void onLoadTrailer(String previewPath,String title,String backdrop) {


        if (sharedPreferences.getBoolean(Constants.WIFI_CHECK, false) &&
                NetworkUtils.isWifiConnected(this)) {

            DialogHelper.showWifiWarning(SerieDetailsActivity.this);

        }else {


            Tools.startTrailer(this,previewPath,title,backdrop);


        }



    }


    // Display Serie Poster
    private void onLoadImage(String imageURL){

        Tools.onLoadMediaCover(getApplicationContext(),binding.MovieCover,imageURL);

    }


    // Display Serie Title
    private void onLoadTitle(String title){

        binding.serieTitle.setText(title);
    }


    // Display Serie Release Date
    @SuppressLint("SetTextI18n")
    private void onLoadDate(String date){

        if (date != null && !date.trim().isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(date);
                binding.mrelease.setText(" - " + sdf2.format(releaseDate));
            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        } else {
            binding.mrelease.setText("");
        }
    }


    // Display Serie Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        binding.serieOverview.setText(synopsis);
    }




    // Report Serie if something wrong
    private void onLoadReport(String title,String posterpath) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;

        EditText editTextMessage = dialog.findViewById(R.id.et_post);
        TextView reportMovieName = dialog.findViewById(R.id.movietitle);
        ImageView imageView = dialog.findViewById(R.id.item_movie_image);


        reportMovieName.setText(title);


        Tools.onLoadMediaCover(getApplicationContext(),imageView,posterpath);


        dialog.findViewById(R.id.bt_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_submit).setOnClickListener(v -> {


            editTextMessage.getText().toString().trim();


            if (editTextMessage.getText() !=null) {

                serieDetailViewModel.sendReport(title,editTextMessage.getText().toString());
                serieDetailViewModel.reportMutableLiveData.observe(SerieDetailsActivity.this, report -> {


                    if (report !=null) {


                        dialog.dismiss();


                        Toast.makeText(this, "Your report has been submitted successfully", Toast.LENGTH_SHORT).show();

                    }


                });

            }


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    // Serie Genres
    private void onLoadGenres(List<Genre> genresList) {
        String genres = "";
        if (genresList != null) {
            for (int i = 0; i < genresList.size(); i++) {
                if (genresList.get(i) == null) continue;
                if (i == genresList.size() - 1) {
                    genres = genres.concat(genresList.get(i).getName());
                } else {
                    genres = genres.concat(genresList.get(i).getName() + ", ");
                }
            }
        }
        binding.mgenres.setText(genres);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (facebookBanner !=null) {

            facebookBanner.destroy();

        }
        binding.bannerContainer.removeAllViews();
        binding = null;
    }



    private void checkAllDataLoaded() {
        if (mSerieLoaded && serieCastLoaded && mEpisodesLoaded) {
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);

        }
    }



    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }


}