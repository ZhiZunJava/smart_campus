package com.smart.web.controller.campus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.SysConfig;
import com.smart.system.service.ISysConfigService;

@RestController
@RequestMapping("/campus/timeTableLayout")
public class ScTimeTableLayoutController extends BaseController {
    private static final String CONFIG_KEY = "campus.schedule.timeTableLayout";

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize("@ss.hasPermi('campus:timetable:list')")
    @GetMapping("/current")
    public AjaxResult current() {
        return success(parseLayout(configService.selectConfigByKey(CONFIG_KEY)));
    }

    @PreAuthorize("@ss.hasPermi('campus:timetable:edit')")
    @PutMapping("/current")
    public AjaxResult save(@RequestBody Map<String, Object> payload) throws Exception {
        String compact = buildCompactLayout(payload);
        SysConfig query = new SysConfig();
        query.setConfigKey(CONFIG_KEY);
        List<SysConfig> configList = configService.selectConfigList(query);
        SysConfig config = configList.isEmpty() ? new SysConfig() : configList.get(0);
        config.setConfigName("课表布局JSON");
        config.setConfigKey(CONFIG_KEY);
        config.setConfigValue(compact);
        config.setConfigType("Y");
        config.setRemark("校园课表布局配置");
        if (config.getConfigId() == null) {
            config.setCreateBy(getUsername());
            configService.insertConfig(config);
        } else {
            config.setUpdateBy(getUsername());
            configService.updateConfig(config);
        }
        return success(parseLayout(compact));
    }

    private Map<String, Object> parseLayout(String configValue) {
        if (StringUtils.isNotEmpty(configValue)) {
            try {
                Map<String, Object> compact = objectMapper.readValue(configValue,
                        new TypeReference<LinkedHashMap<String, Object>>() {
                        });
                if (compact.containsKey("u")) {
                    return expandCompactLayout(compact);
                }
            } catch (Exception ignored) {
            }
        }
        return defaultLayout();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> expandCompactLayout(Map<String, Object> compact) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", compact.getOrDefault("id", 1));
        result.put("nameZh", compact.getOrDefault("n", "默认课表布局"));
        result.put("nameEn", compact.getOrDefault("e", "Default time table layout"));
        result.put("enabled", true);
        List<List<Object>> units = (List<List<Object>>) compact.get("u");
        result.put("courseUnitList", units.stream().map(unit -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("nameZh", unit.get(0));
            item.put("indexNo", unit.get(1));
            item.put("startTime", unit.get(2));
            item.put("endTime", unit.get(3));
            item.put("dayPart", decodeDayPart(String.valueOf(unit.get(4))));
            return item;
        }).toList());
        return result;
    }

    @SuppressWarnings("unchecked")
    private String buildCompactLayout(Map<String, Object> payload) throws Exception {
        Map<String, Object> compact = new LinkedHashMap<>();
        compact.put("id", payload.getOrDefault("id", 1));
        compact.put("n", payload.getOrDefault("nameZh", "默认课表布局"));
        compact.put("e", payload.getOrDefault("nameEn", "Default time table layout"));
        List<Map<String, Object>> unitList = (List<Map<String, Object>>) payload.get("courseUnitList");
        compact.put("u", unitList.stream().map(unit -> List.of(
                unit.getOrDefault("nameZh", ""),
                unit.getOrDefault("indexNo", 0),
                unit.getOrDefault("startTime", 0),
                unit.getOrDefault("endTime", 0),
                encodeDayPart(String.valueOf(unit.getOrDefault("dayPart", "MORNING"))))).toList());
        return objectMapper.writeValueAsString(compact);
    }

    private String encodeDayPart(String value) {
        return switch (value) {
            case "NOON" -> "N";
            case "AFTERNOON" -> "A";
            case "EVENING" -> "E";
            default -> "M";
        };
    }

    private String decodeDayPart(String value) {
        return switch (value) {
            case "N" -> "NOON";
            case "A" -> "AFTERNOON";
            case "E" -> "EVENING";
            default -> "MORNING";
        };
    }

    private Map<String, Object> defaultLayout() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", 1);
        result.put("nameZh", "默认课表布局");
        result.put("nameEn", "Default time table layout");
        result.put("enabled", true);
        result.put("courseUnitList", List.of(
                unit("1", 1, 900, 940, "MORNING"),
                unit("2", 2, 950, 1030, "MORNING"),
                unit("3", 3, 1040, 1120, "MORNING"),
                unit("4", 4, 1130, 1210, "MORNING"),
                unit("中午", 5, 1215, 1240, "NOON"),
                unit("中午", 6, 1245, 1315, "NOON"),
                unit("5", 7, 1320, 1400, "AFTERNOON"),
                unit("6", 8, 1410, 1450, "AFTERNOON"),
                unit("7", 9, 1500, 1540, "AFTERNOON"),
                unit("8", 10, 1550, 1630, "AFTERNOON"),
                unit("9", 11, 1830, 1910, "EVENING"),
                unit("10", 12, 1920, 2000, "EVENING")));
        return result;
    }

    private Map<String, Object> unit(String nameZh, int indexNo, int startTime, int endTime, String dayPart) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("nameZh", nameZh);
        item.put("indexNo", indexNo);
        item.put("startTime", startTime);
        item.put("endTime", endTime);
        item.put("dayPart", dayPart);
        return item;
    }
}
