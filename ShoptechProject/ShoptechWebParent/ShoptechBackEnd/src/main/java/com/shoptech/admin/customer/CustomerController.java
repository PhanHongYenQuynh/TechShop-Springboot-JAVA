package com.shoptech.admin.customer;

import com.shoptech.admin.user.UserCsvExporter;
import com.shoptech.entity.Brand;
import com.shoptech.entity.Country;
import com.shoptech.entity.Customer;
import com.shoptech.entity.User;
import com.shoptech.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService service;

    @GetMapping("/customers")
    public String listFirstPage(Model model) {
        return listByPage(model, 1, "firstName", "asc", null);
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage( Model model,@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        Page<Customer> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<Customer> listCustomers = page.getContent();

        long startCount = (pageNum - 1) * service.CUSTOMERS_PER_PAGE + 1;
        long endCount = startCount + service.CUSTOMERS_PER_PAGE - 1;
        if(endCount > page.getTotalElements())
        {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCustomers", listCustomers);
        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        service.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Customer ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = service.get(id);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/customers";
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = service.get(id);
            List<Country> countries = service.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));

            return "customers/customer_form";

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer,Model model, RedirectAttributes ra) {
        service.save(customer);
        ra.addFlashAttribute("message", "Khách hàng ID " + customer.getId() + " has been updated successfully.");
        return "redirect:/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/customers";
    }
    @GetMapping("/customers/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {

        List<Customer> listCustomers = service.listAll();
        CustomersCsvExporter exporter = new CustomersCsvExporter();
        exporter.export(listCustomers, response);
    }

}

