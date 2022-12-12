package io.kocowa.fmsparser.service;

import io.kocowa.fmsparser.common.config.FmsProperties.ApiName;
import io.kocowa.fmsparser.vo.FmsQueryVO;
import io.kocowa.fmsparser.vo.FmsSeason;
import io.kocowa.fmsparser.vo.FmsSeasonMeta;
import io.kocowa.fmsparser.vo.FmsSeasonReponseEntity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class FmsSeasonRunner implements CommandLineRunner {

  private final FmsSeasonInvoker fmsSeasonInvoker;

  private final String SHEET_NAME = "SEASON";

  @Override
  public void run(String... args) throws Exception {
    log.debug("args [{}]", args.length);

    System.setProperty("file.encoding", "UTF-8");
    log.debug("ENCODING = [{}]", System.getProperty("file.encoding"));

    String apiName = null;
    String idFile = null;
    String outFile = null;

    for (String arg : args) {
      log.debug("args [{}]", arg);
      // if (arg.startsWith("--parent_id=")) {
      //   parentId = arg.substring(arg.indexOf("=") + 1);
      // }
      if (arg.startsWith("--api_name=")) {
        apiName = arg.substring(arg.indexOf("=") + 1);
      }

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

    if (apiName == null) {
      log.error("INVALID Arguments : id_file required");
      log.error(
        "USAGE >> java -jar fmsparser.jar --apiName={season|list} --id_file={id_file} --out_file={out_file}"
      );

      return;
    }

    if (!ApiName.API_NAME_SEASON.apiName().equals(apiName)) {
      log.info(
        "=========================== STOP FmsSeasonRunner!!! =========================="
      );
      return;
    }

    if (idFile == null) {
      log.error("INVALID Arguments : id_file required");
      log.error(
        "USAGE >> java -jar fmsparser.jar --apiName={season|list} --id_file={id_file} --out_file={out_file}"
      );

      return;
    }

    if (outFile == null) {
      log.error("INVALID Arguments : out_file required");
      log.error(
        "USAGE >> java -jar fmsparser.jar --apiName={season|list} --id_file={id_file} --out_file={out_file}"
      );

      return;
    }

    List<String> seasonIdList = makeSeasonIdList(idFile);
    log.debug("seasonIdList size[{}]", seasonIdList.size());

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
    XSSFCellStyle cs = workbook.createCellStyle();
    Row row = sheet.createRow(0);
    int columnIndex = 0;
    Cell cell = row.createCell(columnIndex++);
    cell.setCellValue("season_id");

    cell = row.createCell(columnIndex++);
    cell.setCellValue("title_en");
    cell = row.createCell(columnIndex++);
    cell.setCellValue("tags");
    cell = row.createCell(columnIndex++);
    cell.setCellValue("summary_en");
    cell = row.createCell(columnIndex++);
    cell.setCellValue("description_en");
    cs.setWrapText(true);
    cell.setCellStyle(cs);

    for (String seasonId : seasonIdList) {
      procSeason(seasonId, workbook);
    }

    saveFmsBook(workbook, outFile);
  }

  private void procSeason(String seasonId, XSSFWorkbook workbook) {
    try {
      FmsSeasonReponseEntity res = callFmsSeasonApi(seasonId);
      if (res == null) {
        return;
      }
      log.debug("parent_id [{}] duration [{}]", seasonId, res.getDuration());

      FmsSeason seasonInfo = res.getObject();
      if (seasonInfo == null) {
        log.info("Season Info has not content");
        return;
      }
      writeSeasonData(seasonInfo, workbook);
    } catch (Exception e) {
      log.error("fail to procSeason [{}] - {}", seasonId, e.getMessage(), e);
    }
  }

  private void writeSeasonData(FmsSeason seasonInfo, XSSFWorkbook workbook) {
    XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
    int startRowNo = sheet.getLastRowNum() + 1;
    String seasonId = seasonInfo.getId();

    try {
      FmsSeasonMeta meta = seasonInfo.getMeta();
      String tags = meta
        .getTags()
        .toString()
        .replaceAll("\\[", "")
        .replaceAll("]", "");
      String title = meta.getTitle().getEn();
      String summary = meta.getSummary().getEn();
      String description = meta.getDescription().getEn();
      Row row = sheet.createRow(startRowNo);
      int columnIndex = 0;
      Cell cell = row.createCell(columnIndex++);
      cell.setCellValue(seasonId);
      cell = row.createCell(columnIndex++);
      cell.setCellValue(title);
      cell = row.createCell(columnIndex++);
      cell.setCellValue(tags);
      cell = row.createCell(columnIndex++);
      cell.setCellValue(summary);
      cell = row.createCell(columnIndex++);
      cell.setCellValue(description);
    } catch (Exception e) {
      log.error("fail to write season data - {}", e.getMessage(), e);
      log.info("stop writing SEANSON ID [{}]", seasonId);
    }
  }

  private List<String> makeSeasonIdList(String idFile) {
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

  private void saveFmsBook(XSSFWorkbook workbook, String outFile) {
    try (FileOutputStream fos = new FileOutputStream(new File(outFile))) {
      workbook.write(fos);
    } catch (IOException e) {
      log.error("fail to save Excel[{}] - {}", outFile, e.getMessage(), e);
    }
  }

  private FmsSeasonReponseEntity callFmsSeasonApi(String seasonId) {
    FmsQueryVO query = new FmsQueryVO();
    query.setParentId(seasonId);
    FmsSeasonReponseEntity res = null;
    try {
      res = fmsSeasonInvoker.invoke(query);
    } catch (Exception e) {
      log.error(
        "!!!! Fail to invoke FMS parentId[{}] - {}\n",
        seasonId,
        e.getMessage(),
        e
      );
    }

    return res;
  }
}
