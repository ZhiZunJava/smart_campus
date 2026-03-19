package com.smart.web.controller.campus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScClassroom;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.dto.CourseScheduleAutoArrangeDto;
import com.smart.system.mapper.ScClassCourseMapper;
import com.smart.system.mapper.ScClassroomMapper;
import com.smart.system.service.IScCourseScheduleService;

@RestController
@RequestMapping("/campus/courseSchedule")
public class ScCourseScheduleController extends BaseController {
    @Autowired
    private IScCourseScheduleService scCourseScheduleService;
    @Autowired
    private ScClassCourseMapper scClassCourseMapper;
    @Autowired
    private ScClassroomMapper scClassroomMapper;

    @PreAuthorize("@ss.hasPermi('campus:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourseSchedule scCourseSchedule) {
        startPage();
        List<ScCourseSchedule> list = scCourseScheduleService.selectScCourseScheduleList(scCourseSchedule);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:course:query')")
    @GetMapping("/{scheduleId}")
    public AjaxResult getInfo(@PathVariable Long scheduleId) {
        return success(scCourseScheduleService.selectScCourseScheduleByScheduleId(scheduleId));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:query')")
    @PostMapping("/conflict-check")
    public AjaxResult conflictCheck(@RequestBody ScCourseSchedule scCourseSchedule) {
        Map<String, Object> result = scCourseScheduleService.checkScheduleConflict(scCourseSchedule);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "排课表", businessType = BusinessType.OTHER)
    @PostMapping("/auto-arrange")
    public AjaxResult autoArrange(@RequestBody CourseScheduleAutoArrangeDto request) {
        return success(scCourseScheduleService.autoArrangeCourseSchedule(request, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:add')")
    @Log(title = "排课表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScCourseSchedule scCourseSchedule) {
        scCourseSchedule.setCreateBy(getUsername());
        return toAjax(scCourseScheduleService.insertScCourseSchedule(scCourseSchedule));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "排课表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScCourseSchedule scCourseSchedule) {
        scCourseSchedule.setUpdateBy(getUsername());
        return toAjax(scCourseScheduleService.updateScCourseSchedule(scCourseSchedule));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:remove')")
    @Log(title = "排课表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scheduleIds}")
    public AjaxResult remove(@PathVariable Long[] scheduleIds) {
        return toAjax(scCourseScheduleService.deleteScCourseScheduleByScheduleIds(scheduleIds));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "排课表导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(@RequestParam("file") MultipartFile file,
            @RequestParam("termId") Long termId,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "classId", required = false) Long classId,
            @RequestParam(value = "overwrite", defaultValue = "false") Boolean overwrite) {
        if (file.isEmpty()) {
            return error("上传文件不能为空");
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return error("Excel文件中没有工作表");
            }

            ScClassCourse ccQuery = new ScClassCourse();
            ccQuery.setTermId(termId);
            ccQuery.setStatus("0");
            if (deptId != null) {
                ccQuery.setOpenDeptId(deptId);
            }
            if (classId != null) {
                ccQuery.setClassId(classId);
            }
            List<ScClassCourse> classCourses = scClassCourseMapper.selectScClassCourseList(ccQuery);
            Map<String, ScClassCourse> classCourseIndex = new LinkedHashMap<>();
            for (ScClassCourse cc : classCourses) {
                String key = normalizeKey(cc.getClassName()) + "|" + normalizeKey(cc.getCourseName());
                classCourseIndex.putIfAbsent(key, cc);
            }

            ScClassroom crQuery = new ScClassroom();
            crQuery.setStatus("0");
            List<ScClassroom> classrooms = scClassroomMapper.selectScClassroomList(crQuery);
            Map<String, ScClassroom> classroomIndex = new LinkedHashMap<>();
            for (ScClassroom cr : classrooms) {
                classroomIndex.putIfAbsent(normalizeKey(cr.getClassroomName()), cr);
            }

            if (Boolean.TRUE.equals(overwrite)) {
                List<Long> ccIds = classCourses.stream().map(ScClassCourse::getId).filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (!ccIds.isEmpty()) {
                    ScCourseSchedule delQuery = new ScCourseSchedule();
                    delQuery.setTermId(termId);
                    delQuery.setStatus("0");
                    List<ScCourseSchedule> existing = scCourseScheduleService.selectScCourseScheduleList(delQuery);
                    Long[] idsToDelete = existing.stream()
                            .filter(s -> ccIds.contains(s.getClassCourseId()))
                            .map(ScCourseSchedule::getScheduleId)
                            .filter(Objects::nonNull)
                            .toArray(Long[]::new);
                    if (idsToDelete.length > 0) {
                        scCourseScheduleService.deleteScCourseScheduleByScheduleIds(idsToDelete);
                    }
                }
            }

            int successCount = 0;
            int skipCount = 0;
            List<String> errors = new ArrayList<>();

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }
                String className = getCellString(row, 0);
                String courseName = getCellString(row, 1);
                String weekDayStr = getCellString(row, 2);
                String startSectionStr = getCellString(row, 3);
                String endSectionStr = getCellString(row, 4);
                String classroomName = getCellString(row, 5);
                String weeksText = getCellString(row, 6);

                if (StringUtils.isEmpty(className) && StringUtils.isEmpty(courseName)) {
                    continue;
                }

                String ccKey = normalizeKey(className) + "|" + normalizeKey(courseName);
                ScClassCourse cc = classCourseIndex.get(ccKey);
                if (cc == null) {
                    errors.add("第" + (rowIdx + 1) + "行：未找到班级课程 [" + className + " - " + courseName + "]");
                    skipCount++;
                    continue;
                }

                int weekDay = parseWeekDay(weekDayStr);
                if (weekDay < 1 || weekDay > 7) {
                    errors.add("第" + (rowIdx + 1) + "行：星期格式无效 [" + weekDayStr + "]");
                    skipCount++;
                    continue;
                }

                int startSection = parseIntSafe(startSectionStr, 0);
                int endSection = parseIntSafe(endSectionStr, startSection);
                if (startSection < 1) {
                    errors.add("第" + (rowIdx + 1) + "行：节次无效");
                    skipCount++;
                    continue;
                }

                ScCourseSchedule schedule = new ScCourseSchedule();
                schedule.setTermId(termId);
                schedule.setClassCourseId(cc.getId());
                schedule.setWeekDay(weekDay);
                schedule.setStartSection(startSection);
                schedule.setEndSection(endSection);
                schedule.setWeeksText(StringUtils.isEmpty(weeksText) ? null : weeksText);
                schedule.setStatus("0");
                schedule.setCreateBy(getUsername());
                schedule.setRemark("Excel导入");

                if (StringUtils.isNotEmpty(classroomName)) {
                    ScClassroom cr = classroomIndex.get(normalizeKey(classroomName));
                    if (cr != null) {
                        schedule.setClassroomId(cr.getClassroomId());
                    }
                }

                try {
                    scCourseScheduleService.insertScCourseSchedule(schedule);
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (rowIdx + 1) + "行：" + e.getMessage());
                    skipCount++;
                }
            }

            StringBuilder msg = new StringBuilder();
            msg.append("导入完成，成功 ").append(successCount).append(" 条");
            if (skipCount > 0) {
                msg.append("，跳过 ").append(skipCount).append(" 条");
            }
            if (!errors.isEmpty()) {
                msg.append("。错误：").append(String.join("；", errors.subList(0, Math.min(errors.size(), 5))));
                if (errors.size() > 5) {
                    msg.append("...等 ").append(errors.size()).append(" 个错误");
                }
            }
            return success(msg.toString());
        } catch (IOException e) {
            return error("读取Excel文件失败：" + e.getMessage());
        }
    }

    @GetMapping("/import-template")
    public void importTemplate(HttpServletResponse response) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("排课导入模板");
            Row header = sheet.createRow(0);
            String[] headers = { "班级名称", "课程名称", "星期(一~日)", "开始节次", "结束节次", "教室名称", "周次(如1-16周)" };
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
                sheet.setColumnWidth(i, 4500);
            }
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("计算机1班");
            example.createCell(1).setCellValue("高等数学");
            example.createCell(2).setCellValue("星期一");
            example.createCell(3).setCellValue("1");
            example.createCell(4).setCellValue("2");
            example.createCell(5).setCellValue("A101");
            example.createCell(6).setCellValue("1-16周");

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=schedule_import_template.xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    private String normalizeKey(String value) {
        return StringUtils.isEmpty(value) ? "" : value.trim().toLowerCase();
    }

    private String getCellString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double num = cell.getNumericCellValue();
            if (num == Math.floor(num)) {
                return String.valueOf((long) num);
            }
            return String.valueOf(num);
        }
        return StringUtils.defaultIfEmpty(cell.getStringCellValue(), "").trim();
    }

    private int parseWeekDay(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0;
        }
        String v = value.trim().replace("星期", "");
        switch (v) {
            case "一": case "1": return 1;
            case "二": case "2": return 2;
            case "三": case "3": return 3;
            case "四": case "4": return 4;
            case "五": case "5": return 5;
            case "六": case "6": return 6;
            case "日": case "天": case "7": return 7;
            default: return parseIntSafe(v, 0);
        }
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
