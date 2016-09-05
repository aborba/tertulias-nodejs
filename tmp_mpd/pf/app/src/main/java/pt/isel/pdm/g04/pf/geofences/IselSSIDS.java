package pt.isel.pdm.g04.pf.geofences;

import java.util.HashMap;
import java.util.Map;

import pt.isel.pdm.g04.pf.helpers.Constants;

public class IselSSIDS {
    public final static Map<String, String> Locations = new HashMap<>();

    static {
        //OTHER
        String OTHER_ISEL = "cc:d5:39:e3:e0:de";
        String OTHER_ISEL2 = "cc:d5:39:e3:e9:fe";

        //Building A NIVEL 1

        String BUILDING_A_ZONCE80 = "00:26:5b:7f:ce:88";
        String BUILDING_A_FON_ZON_FREE_INTERNET = "00:26:5b:7f:ce:89";


        //Building E NIVEL 2

        String BUILDING_E_ISEL = "cc:d5:39:e3:e9:f1";
        String BUILDING_E_ISEL2 = "24:01:c7:76:06:df";
        String BUILDING_E_HPPrint = "2c:59:e5:ca:e8:1c";
        String BUILDING_E_eduroam = "cc:d5:39:e3:e9:f2";


        //Building P CERTO NIVEL 4

        String BUILDING_P_ISEL_Event = "cc:d5:39:e3:e9:f0";
        String BUILDING_P_ISEL_Event2 = "cc:d5:39:e3:e0:d0";
        String BUILDING_P_ISEL = "cc:d5:39:e3:e0:d1";
        String BUILDING_P_eduroam = "cc:d5:39:e3:e9:fd";
        String BUILDING_P_eduroam2 = "cc:d5:39:e3:e0:d2";
        String BUILDING_P_eduroam3 = "00:14:6a:c5:04:80";
        String BUILDING_P_eduroam4 = "cc:d5:39:e3:e0:dd";

        //Building M CERTO NIVEL 3

        String BUILDING_M_ISEL = "cc:d5:39:e3:c3:a1";
        String BUILDING_M_ISEL_Event = "cc:d5:39:e3:e0:df";


        //Building STUDENT_PAVILLION NIVEL 2

        String STUDENT_eduroam = "24:01:c7:76:06:dd";
        String STUDENT_eduroam2 = "cc:d5:39:e3:dc:52";
        String STUDENT_eduroam3 = "00:12:43:4e:19:80";
        String STUDENT_ISEL = "cc:d5:39:e3:dc:50";


        //Building F NIVEL 2

        String BUILDING_F_ISEL = "cc:d5:39:e3:c6:80";

        //Building GYM NIVEL 1

        String GYM_YellowBus = "5c:07:6f:98:96:ac";
        String GYM_DIRECT = "9e:80:df:d2:67:e9";
        String GYM_devolo = "f4:06:8d:6e:b1:cc";

        //Building CC NIVEL 1

        String CC_FON_ZON_FREE_INTERNET = "00:05:ca:bd:0c:79";

        //Building C NIVEL 3

        String BUILDING_C_eduroam = "cc:d5:39:e3:c9:f2";

        //Building RESIDENCE NIVEL 1

        String Wlan = "64:70:02:89:70:4e";
        String XIAOLICHEN = "dc:0b:1a:0d:cb:fd";

        Locations.put(OTHER_ISEL, Constants.Isel.Locations.OTHER);
        Locations.put(OTHER_ISEL2, Constants.Isel.Locations.OTHER);
        Locations.put(BUILDING_A_FON_ZON_FREE_INTERNET, Constants.Isel.Locations.BUILDING_A);
        Locations.put(BUILDING_A_ZONCE80, Constants.Isel.Locations.BUILDING_A);
        Locations.put(BUILDING_E_eduroam, Constants.Isel.Locations.BUILDING_E);
        Locations.put(BUILDING_E_HPPrint, Constants.Isel.Locations.BUILDING_E);
        Locations.put(BUILDING_E_ISEL, Constants.Isel.Locations.BUILDING_E);
        Locations.put(BUILDING_E_ISEL2, Constants.Isel.Locations.BUILDING_E);
        Locations.put(BUILDING_P_eduroam, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_P_eduroam2, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_P_eduroam3, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_P_eduroam4, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_P_ISEL, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_P_ISEL_Event, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_P_ISEL_Event2, Constants.Isel.Locations.BUILDING_P);
        Locations.put(BUILDING_C_eduroam, Constants.Isel.Locations.BUILDING_C);
        Locations.put(BUILDING_M_ISEL, Constants.Isel.Locations.BUILDING_M);
        Locations.put(BUILDING_M_ISEL_Event, Constants.Isel.Locations.BUILDING_M);
        Locations.put(STUDENT_eduroam, Constants.Isel.Locations.STUDENT_PAVILLION);
        Locations.put(STUDENT_eduroam2, Constants.Isel.Locations.STUDENT_PAVILLION);
        Locations.put(STUDENT_eduroam3, Constants.Isel.Locations.STUDENT_PAVILLION);
        Locations.put(STUDENT_ISEL, Constants.Isel.Locations.STUDENT_PAVILLION);
        Locations.put(BUILDING_F_ISEL, Constants.Isel.Locations.BUILDING_F);
        Locations.put(GYM_devolo, Constants.Isel.Locations.GYM);
        Locations.put(GYM_DIRECT, Constants.Isel.Locations.GYM);
        Locations.put(GYM_YellowBus, Constants.Isel.Locations.GYM);
        Locations.put(CC_FON_ZON_FREE_INTERNET, Constants.Isel.Locations.CC);
        Locations.put(XIAOLICHEN, Constants.Isel.Locations.RESIDENCE);
        Locations.put(Wlan, Constants.Isel.Locations.RESIDENCE);
    }


}
