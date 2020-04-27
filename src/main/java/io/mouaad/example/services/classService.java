package io.mouaad.example.services;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.mouaad.example.models.data;

@Service
public class classService {

    private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<data> listStatus = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 1 * * *")
    public void fetchData() throws Exception{
        List<data> newList = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        StringReader stringReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
        for (CSVRecord record : records) {
            data state = new data();
            state.setCountry(record.get("Country/Region"));
            state.setState(record.get("Province/State"));
            try{
                state.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
            }catch(Exception e){}
            newList.add(state);
        }

        this.listStatus = newList;
    }

    public List<data> getListStatus() {
        return listStatus;
    }

    public List<data> findPaginated(int currentPage, int pageSize) {
        int startItem = currentPage * pageSize;
        List<data> list;

        if (listStatus.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, listStatus.size());
            list = listStatus.subList(startItem, toIndex);
        }
        return list;
    }
}
