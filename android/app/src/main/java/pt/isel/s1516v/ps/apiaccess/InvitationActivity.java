package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class InvitationActivity extends Activity implements TertuliasApi {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.ia_toolbar),
                R.string.title_activity_tertulia_details,
                Util.IGNORE, Util.IGNORE, null, true);

    }
}
