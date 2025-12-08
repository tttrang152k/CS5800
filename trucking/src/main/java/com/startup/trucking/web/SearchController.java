package com.startup.trucking.web;

import com.startup.trucking.domain.Load;
import com.startup.trucking.service.LoadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final LoadService loadService;

    public SearchController(LoadService loadService) {
        this.loadService = loadService;
    }

    @GetMapping("/loads/search")
    public String searchLoads(@RequestParam(name = "q", required = false) String query,
                              @RequestParam(name = "field", required = false, defaultValue = "id") String field,
                              Model model) {

        List<Load> results = loadService.searchLoads(query, field);

        model.addAttribute("query", query);
        model.addAttribute("field", field);
        model.addAttribute("results", results);

        return "load-search";
    }
}
