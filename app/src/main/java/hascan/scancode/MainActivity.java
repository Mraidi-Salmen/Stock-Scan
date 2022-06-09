package hascan.scancode;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

import hascan.scancode.excel.ExcelHelper;
import hascan.scancode.excel.XlsxCon;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }


    XlsxCon dbAdapter = new XlsxCon(this);

    String scannedData;
    String search;
    ImageButton scanBtn;
    ImageButton searBtn;
    ImageButton dlBtn;
    ImageButton upBtn;
    TextView section_aff;
    TextView famille_aff;
    TextView reference_aff;
    TextView prix_aff;
    TextView disc_aff;
    TextView prix_disc_aff;
    TextView test;
    EditText searText;
    ImageButton close;
    LoadingDialogBar loadingDialogBar;

    int action;

    public static final int requestcode = 1;
    public static final String Tablename = "Demarque";
    public static final String Section = "Section";
    public static final String Famille = "Famille";
    public static final String id = "Id";
    public static final String Reference = "Reference";
    public static final String Price = "Prix";
    public static final String Disc = "Disc";
    public static final String PriceDisc = "PrixDisc";

    Thread thread;

        //On Create Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        action = 0;
        test = (TextView) findViewById(R.id.test_text);
        section_aff = (TextView) findViewById(R.id.section_text);
        famille_aff = (TextView) findViewById(R.id.famille_text);
        reference_aff = (TextView) findViewById(R.id.ref_text);
        prix_aff = (TextView) findViewById(R.id.prix_text);
        disc_aff = (TextView) findViewById(R.id.disc_text);
        prix_disc_aff = (TextView) findViewById(R.id.prixd_text);
        searText = (EditText) findViewById(R.id.sr_text);

        // Scan with code
        scanBtn = (ImageButton) findViewById(R.id.qr_button);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearScreen();
                action = 1;
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setOrientationLocked(true);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(true);
                integrator.setCameraId(0);
                integrator.setCaptureActivity(Capture.class);
                integrator.initiateScan();
            }


        });

        // Search references
        searBtn = (ImageButton) findViewById(R.id.sr_button);
        searBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = searText.getText().toString();
                if (search.toString() != null) {
                    SQLiteDatabase searchdb1 = getApplicationContext().openOrCreateDatabase("HaDisc.db", Context.MODE_PRIVATE, null);
                    Cursor c = searchdb1.rawQuery("select * from Demarque where Reference = '" + search.toString() + "'", null);
                    if (c.getCount() == 0) {
                        ClearScreen();
                        test.setText("Vérifier Votre saisie !! ");
                        return;
                    }
                    StringBuffer section = new StringBuffer();
                    StringBuffer famille = new StringBuffer();
                    StringBuffer reference = new StringBuffer();
                    StringBuffer prix = new StringBuffer();
                    StringBuffer disc = new StringBuffer();
                    StringBuffer prixd = new StringBuffer();
                    while (c.moveToNext()) {

                        section = new StringBuffer();
                        famille = new StringBuffer();
                        reference = new StringBuffer();
                        prix = new StringBuffer();
                        disc = new StringBuffer();
                        prixd = new StringBuffer();

                        section.append(c.getString(0));
                        famille.append(c.getString(1));
                        reference.append(c.getString(3));
                        prix.append(c.getString(4));
                        disc.append(c.getString(5));
                        prixd.append(c.getString(6));


                    }
                    section_aff.setText(section.toString());
                    famille_aff.setText(famille.toString());
                    reference_aff.setText(reference.toString());
                    prix_aff.setText(prix.toString());

                    try {
                        if(Integer.parseInt(disc.toString()) > 0 ) {
                            disc_aff.setText(disc.toString()+"%");
                            prix_disc_aff.setText(prixd.toString());
                        }
                    } catch (NumberFormatException e) {
                        ClearScreen();
                        test.setText("Aucune saisie trouvé !!");
                        e.printStackTrace();
                    }

                }
                else {
                    ClearScreen();
                    test.setText("Aucune saisie trouvé !!");
                    return;
                }
            }
        });

        // Upload data
        loadingDialogBar = new LoadingDialogBar(this);
        upBtn = (ImageButton) findViewById(R.id.up_button);
        checkFilePermissions();
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClearScreen();
                action = 2;
                String[] mimetypes =
                        { "application/vnd.ms-excel", // .xls
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                        };
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("*/*");
                fileintent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
                fileintent.addCategory(Intent.CATEGORY_OPENABLE);


                try {

                    startActivityForResult(fileintent, requestcode);

                } catch (ActivityNotFoundException e) {
                    ClearScreen();
                    test.setText("No activity can handle picking a file. Showing alternatives.");
                }


            }
        });

        // Delete data
        dlBtn = (ImageButton) findViewById(R.id.dl_button);
        dlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClearScreen();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Voulez vous vraiment supprimer la base de données !!");
                builder.setCancelable(true);

                builder.setNegativeButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dbAdapter.open();
                        dbAdapter.delete();
                        dbAdapter.close();
                        test.setText("Supprission avec succées !!");
                    }
                });

                builder.setPositiveButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();



            }

        });

        // Close app
        close = (ImageButton) findViewById(R.id.close_btn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Voulez vous vraiment quitter !!");
                builder.setCancelable(true);

                builder.setNegativeButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                });

                builder.setPositiveButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        }

        //Instant Activity Result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult Result = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );
        if (action == 1) {
            scannedData = Result.getContents();
            if (Result.getContents() != null) {
                SQLiteDatabase searchdb = getApplicationContext().openOrCreateDatabase("HaDisc.db", Context.MODE_PRIVATE, null);
                Cursor c = searchdb.rawQuery("select * from Demarque where id ='" + "0" + scannedData.toString() + "'", null);
                if (c.getCount() == 0) {
                    ClearScreen();
                    test.setText("Ce code a barre n existe pas !!! Verifier votre choix !!");
                    return;
                }

                StringBuffer section = new StringBuffer();
                StringBuffer famille = new StringBuffer();
                StringBuffer reference = new StringBuffer();
                StringBuffer prix = new StringBuffer();
                StringBuffer disc = new StringBuffer();
                StringBuffer prixd = new StringBuffer();

                while (c.moveToNext()){
                    section = new StringBuffer();
                    famille = new StringBuffer();
                    reference = new StringBuffer();
                    prix = new StringBuffer();
                    disc = new StringBuffer();
                    prixd = new StringBuffer();

                    section.append("");
                    section.append(c.getString(0));
                    famille.append(c.getString(1));
                    reference.append(c.getString(3));
                    prix.append(c.getString(4));
                    disc.append(c.getString(5));
                    prixd.append(c.getString(6));

                }


                section_aff.setText(section.toString());
                famille_aff.setText(famille.toString());
                reference_aff.setText(reference.toString());
                prix_aff.setText(prix.toString());

                if(Integer.parseInt(disc.toString()) > 0 ) {
                    disc_aff.setText(disc.toString()+"%");
                    prix_disc_aff.setText(prixd.toString());

                }

            }
            else {
                ClearScreen();
                return;
            }

        }
        else if (action == 2){
            if (data == null)
                return;
            switch (requestCode) {
                case requestcode:
                    Uri path = data.getData();

                    Log.e("File path", data.getData().getPath().toString());

                    if (resultCode == RESULT_OK) {
                        AssetManager am = this.getAssets();
                        InputStream inputStream;
                        try {

                            inputStream = MainActivity.this.getContentResolver().openInputStream(path);
                            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                            XSSFSheet sheet = workbook.getSheetAt(0);
                            inputStream.close();

                            XlsxCon dbAdapter = new XlsxCon(this);
                            dbAdapter.open();
                            ExcelHelper.insertExcelToSqlite(dbAdapter, sheet, loadingDialogBar);
                            dbAdapter.close();
                            test.setText("Téléchargement avec succées !!");

                        } catch (Exception ex) {
                            Log.e("POI Error", ex.getMessage().toString());
                            ClearScreen();
                            test.setText(ex.getMessage().toString() + "Second");
                            ex.printStackTrace();

                        }

                    }

            }

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

        //Clear Screan
    public void ClearScreen(){

        searText.setText("");
        section_aff.setText("");
        famille_aff.setText("");
        reference_aff.setText("");
        prix_aff.setText("");
        disc_aff.setText("");
        prix_disc_aff.setText("");
        test.setText("");
    }

        //Permissions access
    private void checkFilePermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkCallingOrSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1001);
            }else{
                Log.d(TAG,"checkBTPermissions: No need check permissions.");
            }
        }
        }


}

