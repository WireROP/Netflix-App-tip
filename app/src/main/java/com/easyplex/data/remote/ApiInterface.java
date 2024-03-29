package com.easyplex.data.remote;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.ads.Ads;
import com.easyplex.data.model.auth.UserAuthInfo;
import com.easyplex.data.model.episode.EpisodeStream;
import com.easyplex.data.model.report.Report;
import com.easyplex.data.model.search.SearchResponse;
import com.easyplex.data.model.settings.Settings;
import com.easyplex.data.model.status.Status;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.auth.Login;
import com.easyplex.data.model.genres.GenresByID;
import com.easyplex.data.model.genres.GenresData;
import com.easyplex.data.model.credits.MovieCreditsResponse;
import com.easyplex.data.model.upcoming.Upcoming;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that communicates with Your Server Main Api & TheMovieDB API using Retrofit 2 and RxJava 3.
 *
 * @author Yobex.
 */
public interface ApiInterface {



    // Report
    @POST("report")
    @FormUrlEncoded
    Observable<Report> report(@Field("title") String name, @Field("message") String email);


    // Register
    @POST("register")
    @FormUrlEncoded
    Call<Login> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);


    // Login
    @POST("login")
    @FormUrlEncoded
    Call<Login> login(@Field("username") String username, @Field("password") String password);



    // Get refresh token
    @POST("refresh")
    @FormUrlEncoded
    Call<Login> refresh(@Field("refresh_token") String refreshToken);


    // Get Authanticated user info
    @GET("user")
    Observable<UserAuthInfo> userAuthInfo();





    // Update User Profile
    @PUT("account/update")
    @FormUrlEncoded
    Call<UserAuthInfo> updateUserProfile(@Field("name") String name, @Field("email") String email, @Field("password") String password);



    // Update User to Premuim after a successful payment
    @POST("update")
    @FormUrlEncoded
    Observable<UserAuthInfo> userUpdate(@Field("Authorization") String authorization , @Field("Bearer") String bearer);




    // Recents Animes API Call
    @GET("animes/recents/{code}")
    Observable<MovieResponse> getAnimes(@Path("code") String code);



    // Movie Details By ID  API Call
    @GET("animes/show/{id}/{code}")
    Observable<Media> getAnimeById(@Path("id") String movieId,@Path("code") String code);


    // Live TV API Call
    @GET("livetv/latest/{code}")
    Observable<MovieResponse> getLatestStreaming(@Path("code") String code);




    // Upcoming Movies
    @GET("upcoming/latest")
    Observable<MovieResponse> getUpcomingMovies();


    // Upcoming Movies
    @GET("upcoming/show/{id}")
    Observable<Upcoming> getUpcomingMovieDetail(@Path("id") int movieId);



    // Latest Movies API Call
    @GET("movies/latest/{code}")
    Observable<MovieResponse> getMovieLatest(@Path("code") String code);

    // Featured Movies API Call
    @GET("movies/featured/{code}")
    Observable<MovieResponse> getMovieFeatured(@Path("code") String code);

    // Recommended Movies API Call
    @GET("movies/recommended/{code}")
    Observable<MovieResponse> getRecommended(@Path("code") String code);

    // Trending Movies  API Call
    @GET("movies/trending/{code}")
    Observable<MovieResponse> getTrending(@Path("code") String code);


    // This week Movies API Call
    @GET("movies/thisweek/{code}")
    Observable<MovieResponse> getThisWeekMovies(@Path("code") String code);


    // Popular Movies API Call
    @GET("movies/popular/{code}")
    Observable<MovieResponse> getPopularMovies(@Path("code") String code);

    // Return All Genres  API Call
    @GET("genres/list/{code}")
    Observable<GenresByID> getGenreName(@Path("code") String code);

    @GET("genres/movies/show/{id}/{code}")
    Observable<GenresData> getGenreByID(@Path("id") Integer genreId, @Path("code") String code);

    // Movie Details By ID  API Call
    @GET("movies/show/{tmdb}")
    Observable<Media> getMovieByTmdb(@Path("tmdb") String tmdb);



    @GET("genres/series/show/{id}/{code}")
    Observable<GenresData> getSerieById(@Path("id") Integer genreId,@Path("code") String code);

    // Serie Details By ID  API Call

    @GET("series/show/{tmdb}/{code}")
    Observable<Media> getSerieById(@Path("tmdb") String serieTmdb, @Path("code") String code);

    @GET("series/season/{seasons_id}")
    Observable<MovieResponse> getSerieSeasons (@Path("seasons_id") String seasonId);

    // Episode Stream By Episode Imdb  API Call
    @GET("series/episode/{episode_imdb}")
    Observable<MediaStream> getSerieStream(@Path("episode_imdb") String movieId);



    // Episode Substitle By Episode Imdb  API Call
    @GET("series/substitle/{episode_imdb}")
    Observable<EpisodeStream> getEpisodeSubstitle(@Path("episode_imdb") String movieId);


    // Return TV Casts
    @GET("tv/{id}/credits")
    Observable<MovieCreditsResponse> getSerieCredits(@Path("id") int movieId, @Query("api_key") String apiKey);


    // Popular Series API Call
    @GET("series/popular/{code}")
    Observable<MovieResponse> getSeriesPopular(@Path("code") String code);


    // Latest Series API Call
    @GET("series/recents/{code}")
    Observable<MovieResponse> getSeriesRecents(@Path("code") String code);


    // Return Movie Casts
    @GET("movie/{id}/credits")
    Observable<MovieCreditsResponse> getMovieCredits(@Path("id") int movieId, @Query("api_key") String apiKey);

    // Related Movies API Call
    @GET("movies/relateds/{id}")
    Observable<MovieResponse> getRelatedsMovies(@Path("id") int movieId);

    // Suggested Movies API Call
    @GET("movies/suggested/{code}")
    Observable <MovieResponse> getMovieSuggested(@Path("code") String code);


    // Suggested Movies API Call
    @GET("movies/random/{code}")
    Observable <MovieResponse> getMoviRandom(@Path("code") String code);

    // Search API Call
    @GET("search/{id}")
    Observable<SearchResponse> getSearch(@Path("id") String searchquery);


    // Return App Settings
    @GET("settings")
    Observable<Settings> getSettings();


    // Return App Settings
    @GET("status")
    Observable<Status> getStatus();



    // Return Ad Manager
    @GET("ads")
    Observable <Ads> getAdsSettings();
}
