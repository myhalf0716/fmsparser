package io.kocowa.fmsparser.service;

import io.kocowa.fmsparser.common.config.FmsProperties;
import io.kocowa.fmsparser.vo.FmsContent;
import io.kocowa.fmsparser.vo.FmsContent.FmsMeta;
import io.kocowa.fmsparser.vo.FmsQueryVO;
import io.kocowa.fmsparser.vo.FmsReponseEntity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.Array;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Infinispan;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    String idFile = null;
    String outFile = null;

    for (String arg : args) {
      log.debug("args [{}]", arg);
      // if (arg.startsWith("--parent_id=")) {
      //   parentId = arg.substring(arg.indexOf("=") + 1);
      // }
      if (arg.startsWith("--id_file=")) {
        idFile = arg.substring(arg.indexOf("=") + 1);
      }

      if (arg.startsWith("--out_file=")) {
        outFile = arg.substring(arg.indexOf("=") + 1);
      }
      // if (arg.startsWith("--id_path")) {
      //   path = arg.substring(arg.indexOf("=") + 1);
      // }
    }
    if (idFile == null) {
      log.error("INVALID Arguments : id_file required");
      log.error("USAGE >> java -jar fmsparser.jar --id_file={id_file}");

      return;
    }

    if (outFile == null) {
      log.error("INVALID Arguments : out_file required");
      log.error("USAGE >> java -jar fmsparser.jar --out_file={out_file}");

      return;
    }

    List<String> parentIdList = makeParentIdList(idFile);

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
    // sheet.setColumnWidth(0, 15);
    // sheet.setColumnWidth(1, 35);
    // sheet.setColumnWidth(2, 35);
    // sheet.setColumnWidth(3, 70);

    for (String parentId : parentIdList) {
      procSeason(parentId, workbook);
    }

    saveFmsBook(workbook, outFile);
  }

  private List<String> makeParentIdList(String idFile) {
    List<String> parentIdList = new ArrayList<>();
    try (BufferedReader bf = new BufferedReader(new FileReader(idFile))) {
      String idLine = bf.readLine();
      while (idLine != null) {
        String[] ids = idLine.split(",");
        for (String parentId : ids) {
          parentId = parentId.trim();
          if (StringUtils.hasText(parentId)) {
            parentIdList.add(parentId);
          }
        }

        idLine = bf.readLine();
      }
    } catch (IOException e) {
      log.error("fail to read parent id list");
      log.error(e.getMessage(), e);
    }

    return parentIdList;
  }

  private void procSeason(String parentId, XSSFWorkbook workbook) {
    FmsReponseEntity res = callFmsApi(parentId);
    if (res == null) {
      return;
    }

    List<FmsContent> epiList = res.getObjects();
    if (epiList == null || epiList.isEmpty()) {
      log.info("SEASON has no episode");
      return;
    }

    writeSeasonData(epiList, workbook);
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
      log.error(
        "!!!! Fail to invoke FMS parentId[{}] - {}\n",
        parentId,
        e.getMessage(),
        e
      );
    }

    return res;
  }
}
