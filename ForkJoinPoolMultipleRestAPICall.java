package com.concurrency.concurrent.calls.forkjoin;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolMultipleRestAPICall {

   public static void main(final String[] arguments) throws InterruptedException, 
      ExecutionException {
      
      int nThreads = Runtime.getRuntime().availableProcessors();
      System.out.println(nThreads);


      Map<String,String> apiMap = new HashMap<>();
      apiMap.put("india","https://restcountries.com/v3.1/name/");
      apiMap.put("peru","https://restcountries.com/v3.1/name/");
      apiMap.put("italy","https://restcountries.com/v3.1/name/");

      List<String> countries = new ArrayList<>();

      for(Map.Entry<String,String> entry : apiMap.entrySet()){

         countries.add(entry.getValue()+entry.getKey());

      }
      ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
      String finalResult ="";

      for (int i = 0; i < countries.size(); i++) {
         String result = forkJoinPool.invoke(new APICall(countries.get(i)));
         finalResult = finalResult + " ####### "+result;
      }

      System.out.println("FINAL RESULT : " + finalResult);
      
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