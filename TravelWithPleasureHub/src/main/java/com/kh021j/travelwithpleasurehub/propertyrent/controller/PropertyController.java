package com.kh021j.travelwithpleasurehub.propertyrent.controller;

import com.kh021j.travelwithpleasurehub.propertyrent.model.Property;
import com.kh021j.travelwithpleasurehub.propertyrent.model.PropertyImage;
import com.kh021j.travelwithpleasurehub.propertyrent.service.*;
import com.kh021j.travelwithpleasurehub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/api/properties")
@CrossOrigin
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyTypeService propertyTypeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImgurAPIService imgurAPIService;

    @Autowired
    private HEREMapsGeocoderAPIService hereMapsGeocoderAPIService;

    @Autowired
    private PropertyImageService propertyImageService;

    @GetMapping
    public @ResponseBody Iterable<Property> getAllProperties(){
        return propertyService.findAll();
    }

    @GetMapping("/5km")
    public @ResponseBody List<Property> getAllPropertiesWithin5kmRadius(@RequestParam String latitude,
                                                                        @RequestParam String longitude) {
        return propertyService.findBy5kmRadius(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    @PostMapping("/search")
    public @ResponseBody Iterable<Property> filterProperties(@RequestParam String locality,
                                                                  @RequestParam String address,
                                                                  @RequestParam String checkIn,
                                                                  @RequestParam String checkOut){
        return propertyService.filterProperties(locality, address, checkIn, checkOut);
    }

    @PostMapping
    public @ResponseBody Property addProperty(@RequestParam String title,
                                              @RequestParam String locality,
                                              @RequestParam String address,
                                              @RequestParam Integer price,
                                              @RequestParam String description,
                                              @RequestParam MultipartFile[] photos
    ) {
        return propertyService.add(title, locality, address, price, description, photos);
    }

    @PutMapping
    public @ResponseBody Property updateProperty(@RequestBody Property property) {
        return propertyService.update(property);
    }

    @DeleteMapping
    public @ResponseBody void deleteProperty(@RequestBody Property property) {
        propertyService.delete(property);
    }

    @GetMapping("/{id}")
    public @ResponseBody Property getPropertyById(@PathVariable Integer id) {
        return propertyService.findById(id);
    }

    @GetMapping(params = "price")
    public @ResponseBody Iterable<Property> getPropertiesByPriceLessThan(@RequestParam Integer price) {
        return propertyService.findByPriceLessThanEqual(price);
    }

    @GetMapping(params = "sortByPrice")
    public @ResponseBody Iterable<Property> getPropertiesByPriceSortedBy(@RequestParam String sortByPrice) {
        return propertyService.findAllByOrderByPrice(sortByPrice);
    }

    @GetMapping(params = {"since", "until"})
    public @ResponseBody Iterable<Property> getPropertiesByDate(@RequestParam String since, @RequestParam String until) {
        return propertyService.findByAvailabilityInPeriod(since, until);
    }

    @GetMapping(params = {"since", "until", "sortByPrice"})
    public @ResponseBody Iterable<Property> getPropertiesByDateAndSortByPrice(@RequestParam String since,
                                                                              @RequestParam String until,
                                                                              @RequestParam String sortByPrice
    ) {
        return propertyService.findByAvailabilityInPeriodAndSort(since, until, sortByPrice);
    }

}
