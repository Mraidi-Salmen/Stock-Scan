package hascan.scancode.excel;

import android.content.ContentValues;
import android.util.Log;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

import hascan.scancode.LoadingDialogBar;
import hascan.scancode.MainActivity;

public class ExcelHelper extends MainActivity {


    public static final String Tablename = "Demarque";
    public static final String Section = "Section";
    public static final String Famille = "Famille";
    public static final String id = "Id";
    public static final String Reference = "Reference";
    public static final String Price = "Prix";
    public static final String Disc = "Disc";
    public static final String PriceDisc = "PrixDisc";


    public static void insertExcelToSqlite(XlsxCon dbAdapter, Sheet sheet, LoadingDialogBar loadingDialogBar) {

        loadingDialogBar.ShowDialog("Loading ..");

        new Thread(new Runnable() {

            @Override
            public void run() {



                for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ) {
                    Row row = rit.next();

                    ContentValues contentValues = new ContentValues();
                    row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                    row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                    row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                    row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                    row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                    row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                    row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);


                    contentValues.put(Section, row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    contentValues.put(Famille, row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    contentValues.put(id, row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    contentValues.put(Reference, row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    contentValues.put(Price, row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    contentValues.put(Disc, row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                    contentValues.put(PriceDisc, row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());


                    try {
                        if (dbAdapter.insert("Demarque", contentValues) < 0) {
                            return;
                        }
                    } catch (Exception ex) {
                        Log.d("Exception in importing", ex.getMessage().toString());
                    }

                }

            loadingDialogBar.HideDialog();
            }

        }).start();



    }


}
