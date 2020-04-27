package io.mouaad.example.controllers;

import io.mouaad.example.services.classService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import io.mouaad.example.models.data;

@Controller
public class HomeController {

    @Autowired
    classService classService;

    @GetMapping(value = "/")
    public String home(Model model, @RequestParam("page") Optional<Integer> page){
        int currentPage = page.orElse(1);
        int pageSize = 50;

        int totalPages = classService.getListStatus().size()/pageSize;
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        List<data> pages = classService.findPaginated(currentPage - 1, pageSize);
        model.addAttribute("states",  pages);
        model.addAttribute("pageSize",  pageSize);
        model.addAttribute("statesSize",  pages.size());
        model.addAttribute("currentPage",  currentPage);
        return "index";
    }

    @PostMapping("/")
    public String search(@RequestBody Optional<String> search, Model model){
        String serch = search.get().replace("search=","");
        model.addAttribute("states", classService.getListStatus().stream().filter(data -> data.getCountry().equals(serch)).collect(Collectors.toList()));
        return "index";
    }
}
