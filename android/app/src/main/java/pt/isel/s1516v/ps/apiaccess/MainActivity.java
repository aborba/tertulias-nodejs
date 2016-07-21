package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.squareup.picasso.Picasso;

import pt.isel.s1516v.ps.apiaccess.flow.GetHomeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetData;
import pt.isel.s1516v.ps.apiaccess.flow.GetMeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetTertuliasCallback;
import pt.isel.s1516v.ps.apiaccess.flow.LoginCallback;
import pt.isel.s1516v.ps.apiaccess.flow.PostRegisterCallback;
import pt.isel.s1516v.ps.apiaccess.helpers.LoginStatus;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.NewTertuliaActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.SearchPublicTertuliaActivity;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class MainActivity extends Activity implements TertuliasApi {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private static String[] menuTitles;

    public static ApiLinks apiHome = new ApiLinks(null);
    public static ApiLinks apiLinks = new ApiLinks(null);

    private final static String TERTULIAS_KEY = "tertulias";
    private final static String API_ROOT_END_POINT = "/";

    private ImageView userImage;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ProgressBar progressBar;
    private TertuliasArrayRvAdapter listViewAdapter;
    public static Tertulia[] tertulias;
    private MobileServiceUser mUser;
    private LoginStatus loginStatus = null;

//    region Activity lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (menuTitles == null)
            menuTitles = getResources().getStringArray(R.array.main_activity_drawer_list_items);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.planets_list);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menuTitles));
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Login
                        doLoginAndFetch(MainActivity.this, progressBar);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 1: // Logout
                        doLogout(MainActivity.this);
                        break;
                    case 2: // Refresh
                        requestTertuliasList(MainActivity.this);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    default:
                }
            }
        });

        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }
        setupViews();
        userImage = (ImageView) findViewById(R.id.mtl_user_picture);
        if (!Util.isSignedIn(this))
            Picasso.with(this).load(R.mipmap.tertulias).into(userImage);

        progressBar = (ProgressBar) findViewById(R.id.mtl_progressbar);
        emptyView = (TextView) findViewById(R.id.mtl_empty_view);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);

        loadInstanceState(savedInstanceState);

        doLoginAndFetch(this, progressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NewTertuliaActivity.REQUEST_CODE:
            case SearchPublicTertuliaActivity.REQUEST_CODE:
            case TertuliaDetailsActivity.REQUEST_CODE:
                if (resultCode == RESULT_FAIL) return;
                requestTertuliasList(this);
                break;
            default:
                break;
        }
    }

//    endregion

//  region Instance State management

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(TERTULIAS_KEY, tertulias);
    }

    private void loadInstanceState(Bundle inState) {
        if (inState == null || !inState.containsKey(TERTULIAS_KEY))
            return;
        Parcelable[] parcelables = inState.getParcelableArray(TERTULIAS_KEY);
        if (parcelables == null) {
            tertulias = new Tertulia[0];
            return;
        }
        tertulias = new Tertulia[parcelables.length];
        for (int i = 0; i < parcelables.length; i++)
            tertulias[i] = (Tertulia) parcelables[i];
    }

//  endregion

//    region Click Handlers

    public void onClickNewTertulia(final View view) {
        if (apiLinks == null || apiLinks.isEmpty()) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        Intent intent = new Intent(this, NewTertuliaActivity.class);
        intent.putExtra(NewTertuliaActivity.LINKS_LABEL, apiLinks);
        intent.putExtra(NewTertuliaActivity.ROUTE_END_POINT_LABEL, apiLinks.getRoute(LINK_CREATE));
        intent.putExtra(NewTertuliaActivity.ROUTE_METHOD_LABEL, apiLinks.getMethod(LINK_CREATE));
        intent.putExtra(NewTertuliaActivity.MY_TERTULIAS, tertulias);
        startActivityForResult(intent, NewTertuliaActivity.REQUEST_CODE);
    }

    public void onClickSearchTertulia(final View view) {
        if (apiLinks == null || apiLinks.isEmpty()) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        Intent intent = new Intent(this, SearchPublicTertuliaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(SearchPublicTertuliaActivity.LINKS_LABEL, apiLinks);
        startActivityForResult(intent, SearchPublicTertuliaActivity.REQUEST_CODE);
    }

//    endregion

//     region Private Methods

    private void setupViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mtl_toolbar);
        Util.setupToolBar(toolbar,
                R.string.title_activity_list_tertulias,
                R.string.title_activity_list_tertulias,
                Util.IGNORE, // R.menu.activity_main_menu,
                null
//                new Toolbar.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.amm_login:
//                                doLoginAndFetch(MainActivity.this, progressBar);
//                                return true;
//                            case R.id.amm_logout:
//                                doLogout(MainActivity.this);
//                                return true;
//                            case R.id.amm_reload:
//                                requestTertuliasList(MainActivity.this);
//                                return true;
//                            default:
//                                return false;
//                            }
//                    }
//                }
 );
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    return;
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.mtl_RecyclerView);
        listViewAdapter = new TertuliasArrayRvAdapter(this, tertulias != null ? tertulias : new Tertulia[0]);
        Util.setupAdapter(this, recyclerView, listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
    }

    private void updateStatusAlert(Boolean isLogin, int alertMessage) {
        if (loginStatus != null)
            loginStatus.set(isLogin);
        Util.longSnack(findViewById(android.R.id.content), alertMessage);
    }

    private void request(Context ctx, String route, String httpMethod, FutureCallback<JsonElement> callback) {
        if (route == null || httpMethod == null) {
            Util.longSnack(((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content),
                    R.string.main_activity_server_error_exiting);
            finish();
        }
        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }

        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        ListenableFuture<JsonElement> future = cli.invokeApi(route, null, httpMethod, null);
        if (callback != null) Futures.addCallback(future, callback);
    }

    private void requestTertuliasList(Context ctx) {
        if (apiHome == null) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        request(ctx, apiHome.getRoute("tertulias"), apiHome.getMethod("tertulias"), new GetTertuliasCallback(ctx, recyclerView, emptyView, progressBar, null, null));
    }

    //    region User session management

    private void requestLogin(final Context ctx, final FutureCallback<MobileServiceUser> callback) {
        final MobileServiceClient cli = Util.getMobileServiceClient(this);
        if (Util.isTokenValid(Util.getAuthenticationToken(cli), 15000) || cli.isLoginInProgress()) {
            callback.onSuccess(cli.getCurrentUser());
            return;
        }
        Util.lockOrientation(this);
        Futures.addCallback(
                cli.login(MobileServiceAuthenticationProvider.Google),
                new FutureCallback<MobileServiceUser>() {
                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        Util.unlockOrientation(MainActivity.this);
                        callback.onSuccess(user);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Util.unlockOrientation(MainActivity.this);
                        callback.onFailure(t);
                    }
                }
        );
    }

    private void doLoginAndFetch(Context ctx, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        GetData<JsonElement> getTertulias = new GetData<>(ctx, "tertulias", apiHome);
        GetTertuliasCallback getTertuliasCallback = new GetTertuliasCallback(ctx, recyclerView, emptyView, progressBar, null, null);

        GetData<JsonElement> getMe = new GetData<>(ctx, "me", apiHome);
        GetMeCallback getMeCallback = new GetMeCallback(ctx, null, getTertulias, getTertuliasCallback, userImage);

        GetData<JsonElement> postRegister = new GetData<>(ctx, "registration", apiHome);
        PostRegisterCallback postRegisterCallback = new PostRegisterCallback(ctx, null, getMe, getMeCallback);

        GetData<JsonElement> getHome = new GetData<>(ctx, API_ROOT_END_POINT, null);
        GetHomeCallback getHomeCallback = new GetHomeCallback(ctx, null, postRegister, postRegisterCallback);

        LoginCallback loginCallback = new LoginCallback(ctx, null, getHome, getHomeCallback);
        requestLogin(ctx, loginCallback);
    }

    private void doLogout(final Context ctx) {
        Futures.addCallback(
                Util.getMobileServiceClient(this).logout(),
                new FutureCallback<MobileServiceUser>() {

                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        mUser = null;
                        tertulias = new Tertulia[0];

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(ctx).load(R.mipmap.tertulias).into(userImage);
//                                userImage.setVisibility(View.INVISIBLE);
                                listViewAdapter = new TertuliasArrayRvAdapter(MainActivity.this, tertulias != null ? tertulias : new Tertulia[0]);
                                recyclerView.setAdapter(listViewAdapter);
                            }
                        };
                        Looper.prepare();
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(runnable);

                        if (loginStatus != null)
                            loginStatus.reset(R.string.main_activity_logout_succeed_message);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        updateStatusAlert(LoginStatus.SIGNED_IN, R.string.main_activity_logout_failed_message);
                    }
                }
        );
    }

    //    endregion

//    endregion
}
