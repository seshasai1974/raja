package com.concurrency.concurrent.calls;

import com.concurrency.concurrent.calls.forkjoin.ForkJoinPoolMultipleRestAPICall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping(path="/v1")
public class APIController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping(path = "/apis")
    public void getAllAPICalls() throws ExecutionException, InterruptedException {

        Map<String,String> apiMap = new HashMap<>();
        apiMap.put("india","https://restcountries.com/v3.1/name/");
        apiMap.put("peru","https://restcountries.com/v3.1/name/");
        apiMap.put("italy","https://restcountries.com/v3.1/name/");

        List<String> countries = new ArrayList<>();

        for(Map.Entry<String,String> entry : apiMap.entrySet()){

            countries.add(entry.getValue()+entry.getKey());

        }

        List<CompletableFuture<String>> allFutures = new ArrayList<>();

        for (int i = 0; i < countries.size(); i++) {
            allFutures.add(asyncService.callMsgService(countries.get(i)));
        }



        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

        String finalResponse = "";
        //JSONArray jsonArray = new JSONArray(JSON);

        //JSONObject json =

        for (int i = 0; i < countries.size(); i++) {
            System.out.println("--------------------------------------");
            System.out.println("response: " + allFutures.get(i).get().toString());
            //JSONObject obj=new JSONObject(allFutures.get(i).get().toString());
            //jsonArray.put("CountriesList" +obj);
            //obj =  allFutures.get(i).get().toString();
            //finalResponse = finalResponse + allFutures.get(i).get().toString();
        }

    }

    @GetMapping(path = "/fork-apis")
    public void getAllForkJoinAPICalls(){
        int nThreads = Runtime.getRuntime().availableProcessors();
        System.out.println(nThreads);


        Map<String,String> apiMap = new HashMap<>();
        apiMap.put("india","https://restcountries.com/v3.1/name/");
        apiMap.put("peru","https://restcountries.com/v3.1/name/");
        apiMap.put("france","https://restcountries.com/v3.1/name/");

        List<String> apiList = new ArrayList<>();

        for(Map.Entry<String,String> entry : apiMap.entrySet()){

            apiList.add(entry.getValue()+entry.getKey());

        }

        ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
        String finalResult ="";
        List<ForkJoinTask> list = new ArrayList<>();

        for (int i = 0; i < apiList.size(); i++) {

           // String result = forkJoinPool.invoke(new APIController.APICall(apiList.get(i)));
            //list.add(new APICall(apiList.get(i)));
            APICall apiTask = new APICall(apiList.get(i));
            apiTask.fork();
            apiTask.join();
            String myResult = apiTask.getRawResult();
            finalResult = finalResult + " ####### "+myResult;


        }



       // ForkJoinTask.invokeAll(list);


        System.out.println("FINAL RESULT API CONTROLLER using myresult : " + finalResult);
    }

    static class APICall extends RecursiveTask<String> {

        final String url;
        APICall(String url) {
            this.url = url;

        }

        protected String compute() {
            RestTemplate restTemplate = new RestTemplate();
            final String response = restTemplate.getForObject(url, String.class);

            return response;

        }
    }

}
