package io.kocowa.fmsparser.service;

import io.kocowa.fmsparser.common.config.FmsProperties;
import io.kocowa.fmsparser.vo.FmsContent;
import io.kocowa.fmsparser.vo.FmsContent.FmsMeta;
import io.kocowa.fmsparser.vo.FmsQueryVO;
import io.kocowa.fmsparser.vo.FmsReponseEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FmsRunner implements CommandLineRunner {

  private final FmsInvoker fmsInvoker;
  private final String SHEET_NAME = "FMS";

  @Override
  public void run(String... args) throws Exception {
    log.debug("args [{}]", args.length);

    System.setProperty("file.encoding", "UTF-8");
    log.debug("ENCODING = [{}]", System.getProperty("file.encoding"));

    String parentId = null;
    String outFile = null;

    for (String arg : args) {
      log.debug("args [{}]", arg);
      if (arg.startsWith("--parent_id=")) {
        parentId = arg.substring(arg.indexOf("=") + 1);
      }
      if (arg.startsWith("--out=")) {
        outFile = arg.substring(arg.indexOf("=") + 1);
      }
      // if (arg.startsWith("--id_path")) {
      //   path = arg.substring(arg.indexOf("=") + 1);
      // }
    }
    if (parentId == null) {
      log.error("INVALID Arguments : Parent ID required");
      log.error("USAGE >> java -jar fmsparser.jar --parent_id={parent_id}");

      return;
    }

    if (outFile == null) {
      outFile = parentId.concat(".xlsx");
    }

    FmsReponseEntity res = callFmsApi(parentId);
    if (res == null) {
      log.info("FMS API return null");
      return;
    }

    List<FmsContent> epiList = res.getObjects();
    if (epiList == null || epiList.isEmpty()) {
      log.info("SEASON has no episode");
      return;
    }

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
    sheet.setColumnWidth(0, 15);
    sheet.setColumnWidth(1, 35);
    sheet.setColumnWidth(2, 35);
    sheet.setColumnWidth(3, 70);

    writeSeasonData(epiList, workbook);
    saveFmsBook(workbook, outFile);
  }

  private void saveFmsBook(XSSFWorkbook workbook, String outFile) {
    try (FileOutputStream fos = new FileOutputStream(new File(outFile))) {
      workbook.write(fos);
    } catch (IOException e) {
      log.error("fail to save Excel[{}] - {}", outFile, e.getMessage(), e);
    }
  }

  private void writeSeasonData(
    List<FmsContent> epiList,
    XSSFWorkbook workbook
  ) {
    XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
    XSSFCellStyle cs = workbook.createCellStyle();
    Row row = sheet.createRow(0);
    int columnIndex = 0;
    Cell cell = row.createCell(columnIndex++);
    cell.setCellValue("parent_id");

    cell = row.createCell(columnIndex++);
    cell.setCellValue("title_en");
    cell = row.createCell(columnIndex++);
    cell.setCellValue("summary_en");
    cell = row.createCell(columnIndex++);
    cell.setCellValue("description_en");
    cs.setWrapText(true);
    cell.setCellStyle(cs);

    for (int rowNo = 0; rowNo < epiList.size(); rowNo++) {
      FmsContent episode = epiList.get(rowNo);
      if (episode == null || episode.getMeta() == null) {
        continue;
      }

      try {
        row = sheet.createRow(rowNo + 1);
        columnIndex = 0;
        FmsMeta meta = episode.getMeta();

        cell = row.createCell(columnIndex++);
        cell.setCellValue(episode.getPreantId());
        cell = row.createCell(columnIndex++);
        cell.setCellValue(meta.getTitle().getEn());
        cell = row.createCell(columnIndex++);
        cell.setCellValue(meta.getSummary().getEn());
        cell = row.createCell(columnIndex++);
        cell.setCellValue(meta.getDescription().getEn());
      } catch (Exception e) {
        log.error("fail to write episode data - {}", e.getMessage(), e);
        log.info("EPISODE [{}]", episode);
        log.info(
          "stop writing episode [{}] of parent_id [{}] Season [{}]",
          episode.getEpisodeNumber(),
          episode.getPreantId(),
          episode.getSeasonNumber()
        );
        break;
      }
    }
  }

  private FmsReponseEntity callFmsApi(String parentId) {
    FmsQueryVO query = new FmsQueryVO();
    query.setParentId(parentId);
    FmsReponseEntity res = null;
    try {
      res = fmsInvoker.invoke(query);
    } catch (Exception e) {
      log.error("!!!! Fail to invoke FMS - {}\n", e.getMessage(), e);
    }

    return res;
    // List<FmsContent> fmsList = res.getObjects();
    // log.debug("FmsReponseEntity content list[{}]", fmsList.size());
    // for (FmsContent content : fmsList) {
    //   log.debug("CONTENT [{}]", content);
    // }

  }
}
