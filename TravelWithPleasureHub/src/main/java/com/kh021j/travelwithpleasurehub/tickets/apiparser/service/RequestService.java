package com.kh021j.travelwithpleasurehub.tickets.apiparser.service;


import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import org.springframework.stereotype.Service;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.kh021j.travelwithpleasurehub.tickets.apiparser.model.RequestModel;

@Service
public class RequestService {

    private String formatDate(String pickedPlace) {

        return String.format("%s-sky", pickedPlace);
    }

    private HttpRequestWithBody setRequestHeader() {

        return Unirest.post("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0")
                .header("X-RapidAPI-Key", "bcd36fed2dmsh5b3b47a2027f3f3p156b2ejsn65d554031160")
                .header("Content-Type", "application/x-www-form-urlencoded");
    }

    private HttpResponse buildJson(RequestModel requestModel) throws UnirestException {

        HttpRequestWithBody session = setRequestHeader();

        return session.field("country", requestModel.getCountry())
                .field("locale", requestModel.getLocale())
                .field("originPlace", formatDate(requestModel.getOriginPlace()))
                .field("destinationPlace", formatDate(requestModel.getDestinationPlace()))
                .field("outboundDate", requestModel.getOutboundDate())
                .field("adults", requestModel.getAdults())
                .field("children", requestModel.getChildren())
                .field("infants", requestModel.getInfants())
                .field("cabinClass", requestModel.getCabinType().toString().toLowerCase())
                .asJson();
    }

    private String getSessionKey(RequestModel requestModel) throws UnirestException {
        String location = buildJson(requestModel).getHeaders().getFirst("Location");
        String[] temp = location.split("/");

        return temp[temp.length - 1];
    }

    private HttpResponse<JsonNode> getAllItineraries(String sessionKey) throws UnirestException {

        String url = String.format("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/uk2/v1.0/%s?pageIndex=0&pageSize=10", sessionKey);

        return Unirest.get(url)
                .header("X-RapidAPI-Key", "bcd36fed2dmsh5b3b47a2027f3f3p156b2ejsn65d554031160").asJson();
    }

    public JsonNode sendResponseToController(RequestModel requestModel) throws UnirestException {

        return getAllItineraries(getSessionKey(requestModel)).getBody();
    }
}
