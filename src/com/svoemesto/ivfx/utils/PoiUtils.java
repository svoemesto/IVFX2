package com.svoemesto.ivfx.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.poi.xssf.eventusermodel.XSSFBReader;

public class PoiUtils {

    public static void main(String[] args) throws IOException {

        String fileName = "D:\\Dropbox\\_MAIN\\01 - 06 Зарплата 2021_Бюджет-Внебюджет (28.09.21).xls";

        readFromExcel();

    }

    public static HSSFWorkbook readWorkbook(String filename) {
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filename));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            return wb;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void writeWorkbook(HSSFWorkbook wb, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
        }
        catch (Exception e) {
            //Обработка ошибки
        }
    }

    public static void writeIntoExcel(String file) throws FileNotFoundException, IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Birthdays");

        // Нумерация начинается с нуля
        Row row = sheet.createRow(0);

        // Мы запишем имя и дату в два столбца
        // имя будет String, а дата рождения --- Date,
        // формата dd.mm.yyyy
        Cell name = row.createCell(0);
        name.setCellValue("John");

        Cell birthdate = row.createCell(1);

        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));
        birthdate.setCellStyle(dateStyle);


//        // Нумерация лет начинается с 1900-го
        birthdate.setCellValue(new Date(110, 10, 10));

        // Меняем размер столбца
        sheet.autoSizeColumn(1);

        // Записываем всё в файл
        book.write(new FileOutputStream(file));
        book.close();
    }

    public static void readFromExcel() throws IOException{

        String fileName1 = "D:\\Dropbox\\_MAIN\\01 - 06 Зарплата 2021_Бюджет-Внебюджет (04.10.21).xls";
        String fileName2 = "D:\\Dropbox\\_MAIN\\01 - 06 Зарплата 2021_Бюджет-Внебюджет (04.10.21).xls - результат.xls";

        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(fileName1));

        List<Integer> listColumnsAsId = new ArrayList<>();
        listColumnsAsId.add(0);
        listColumnsAsId.add(1);
        listColumnsAsId.add(2);
        listColumnsAsId.add(3);

        List<Integer> listColumnsToCopy = new ArrayList<>();
        listColumnsToCopy.add(0);
        listColumnsToCopy.add(1);
        listColumnsToCopy.add(2);
        listColumnsToCopy.add(3);
        listColumnsToCopy.add(4);
        listColumnsToCopy.add(6);
        listColumnsToCopy.add(7);

        int columnToFind = 5;

        List<String> listOfValues = new ArrayList<>();
        listOfValues.add("Бюджет");
        listOfValues.add("Внебюджет");

        HSSFSheet myExcelSheet = myExcelBook.getSheet("Исх. данные");
        HSSFSheet myExcelSheetNew = myExcelBook.createSheet("Исх. данные - добавление");

        List<Integer> listFoundedRows = new ArrayList<>();

        int countAddedRows = 0;

        int countRows = myExcelSheet.getLastRowNum();

        for (int iRow1 = 1; iRow1 <= countRows+1; iRow1++) {
            Row row1 = myExcelSheet.getRow(iRow1-1);

            if (row1.getRowNum() > 0) {
                boolean alreadyAdded = false;
                for (Integer rowNum : listFoundedRows) {
                    if (rowNum == row1.getRowNum()) {
                        alreadyAdded = true;
                        break;
                    }
                }

                if (!alreadyAdded) {

                    System.out.println("ПЕРВЫЙ " + row1.getCell(0).getDateCellValue() + " - " + row1.getCell(1).getStringCellValue() + " " + row1.getCell(2).getStringCellValue() + " " + row1.getCell(3).getStringCellValue());
                    int countDoubles = 1;

                    for (int iRow2 = iRow1+1; iRow2 <= countRows+1; iRow2++) {
                        Row row2 = myExcelSheet.getRow(iRow2-1);

                        if (row1.getCell(0).getNumericCellValue() == row2.getCell(0).getNumericCellValue() &&
                                row1.getCell(1).getStringCellValue().equals(row2.getCell(1).getStringCellValue()) &&
                                row1.getCell(2).getStringCellValue().equals(row2.getCell(2).getStringCellValue()) &&
                                row1.getCell(3).getStringCellValue().equals(row2.getCell(3).getStringCellValue()) &&
                                row1.getCell(4).getStringCellValue().equals(row2.getCell(4).getStringCellValue()) &&
                                row1.getCell(6).getStringCellValue().equals(row2.getCell(6).getStringCellValue())) {
                            countDoubles++;
                            listFoundedRows.add(row2.getRowNum());
                        }

                    }
                    if (countDoubles == 1) {
                        System.out.println(row1.getCell(0).getDateCellValue() + " - " + row1.getCell(1).getStringCellValue() + " " + row1.getCell(2).getStringCellValue() + " " + row1.getCell(3).getStringCellValue());
                        countAddedRows++;
                        Row rowNew = myExcelSheetNew.createRow(countAddedRows);
                        Cell cell0 = rowNew.createCell(0, CellType.NUMERIC);
                        Cell cell1 = rowNew.createCell(1, CellType.STRING);
                        Cell cell2 = rowNew.createCell(2, CellType.STRING);
                        Cell cell3 = rowNew.createCell(3, CellType.STRING);
                        Cell cell4 = rowNew.createCell(4, CellType.STRING);
                        Cell cell5 = rowNew.createCell(5, CellType.STRING);
                        Cell cell6 = rowNew.createCell(6, CellType.STRING);
                        Cell cell7 = rowNew.createCell(7, CellType.STRING);

                        cell0.setCellValue(row1.getCell(0).getNumericCellValue());
                        cell1.setCellValue(row1.getCell(1).getStringCellValue());
                        cell2.setCellValue(row1.getCell(2).getStringCellValue());
                        cell3.setCellValue(row1.getCell(3).getStringCellValue());
                        cell4.setCellValue(row1.getCell(4).getStringCellValue());
                        cell5.setCellValue(row1.getCell(5).getStringCellValue().equals("Бюджет") ? "Внебюджет" : "Бюджет");
                        cell6.setCellValue(row1.getCell(6).getStringCellValue());
                        cell7.setCellValue(row1.getCell(7).getStringCellValue());

                    }


                }

            }

        }


//        for (Row rowww : myExcelSheet) {
//            for (Cell cell : rowww) {
//                if (cell.getCellType() == CellType.STRING) System.out.println(cell.getColumnIndex() + " - " +  cell.getStringCellValue());
//                if (cell.getCellType() == CellType.NUMERIC) System.out.println(cell.getColumnIndex() + " - " +  cell.getNumericCellValue());
//            }
//        }



//        if(row.getCell(0).getCellType() == CellType.STRING){
//            String name = row.getCell(0).getStringCellValue();
//            System.out.println("name : " + name);
//        }

//        if(row.getCell(1).getCellType() == CellType.NUMERIC){
//            Date birthdate = row.getCell(1).getDateCellValue();
//            System.out.println("birthdate :" + birthdate);
//        }

//        myExcelBook.close();

        myExcelBook.write(new FileOutputStream(fileName2));
        myExcelBook.close();

    }

}
