// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

final class TestMain {

  public static void main(String[] args) {
//
//    String s = "{ \"name\":{\"first\":\"sam\", \"last\":\"doe\" } }";
//    String in = "{ \"name\":\":\", \"age\" : \"17\", \"birthdate\" : { \"month\" : \"jan\", \"day\" : { \"1st\" : \"0\", \"2nd\" : \"1\"}, \"year\" : \"1999 }\"}}";
//    String s2 = "{ \"name\": \"last\"}";
//    String s1 = "{ \"{name}\":{\"first\":\"sam\", \"last\":\"doe\" } }";
//    String s = "{ \"name\":{\"first\":\"sam\", \"last\":\"doe\" } }";
//    MyJSONParser parsed = new MyJSONParser();
//    try {
//      JSON j = parsed.parse(s).getObject("name");
//      ArrayList<String> a = new ArrayList<String>();
//      j.getStrings(a);
//      for(String ss: a){
//        System.out.println(ss);
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//
    final Tester tests = new Tester();

    tests.add("Empty Object", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ }");

        final Collection<String> strings = new HashSet<>();
        obj.getStrings(strings);

        Asserts.isEqual(strings.size(), 0);

        final Collection<String> objects = new HashSet<>();
        obj.getObjects(objects);

        Asserts.isEqual(objects.size(), 0);
      }
    });

    tests.add("String Value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"name\":\"sam doe\" }");

        Asserts.isEqual("sam doe", obj.getString("name"));
     }
    });

    tests.add("Object Value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {

        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"name\":{\"first\":\"sam\", \"last\":\"doe\" } }");

        final JSON nameObj = obj.getObject("name");

        Asserts.isNotNull(nameObj);
        Asserts.isEqual("sam", nameObj.getString("first"));
        Asserts.isEqual("doe", nameObj.getString("last"));
      }
    });

    tests.add("Valid Key", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"{name}\":\"sam doe\" }");

        Asserts.isEqual("sam doe", obj.getString("{name}"));
      }
    });

    tests.add("Valid Value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"name\":\"sam, :doe}\" }");

        Asserts.isEqual("sam, :doe}", obj.getString("name"));
      }
    });

    tests.add("Nested Object Value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {

        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"name\":\":\", \"age\" : \"18\", \"birthdate\" : { \"month\" : \"jan\", \"day\" : { \"1st\" : \"0\", \"2nd\" : \"1,\"}, \"year\" : \"1999 }\"}}");

        final JSON birthObj = obj.getObject("birthdate");
        final JSON dayObj = birthObj.getObject("day");

        Asserts.isNotNull(birthObj);
        Asserts.isEqual("jan", birthObj.getString("month"));
        // added ' }' to the value of year
        // to test whether '}' value corrupted the parser(it didn't).
        Asserts.isEqual("1999 }", birthObj.getString("year"));

        Asserts.isNotNull(dayObj);
        Asserts.isEqual("0", dayObj.getString("1st"));
        // added ',' to the value of '2nd' to test
        // whether commas corrupted the parser(they didn't).
        Asserts.isEqual("1,", dayObj.getString("2nd"));
      }
    });

    /*
    * Testing for invalid JSON-Lite schemas.
    * */

    tests.add("Illegal character after given value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '}' after value should not be there = throw error.
        final JSON obj = parser.parse("{ \"name\": \"sam doe\" ,}");
      }
    });

    tests.add("Illegal character after given key", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '}' after colon should not be there = throw error.
        final JSON obj = parser.parse("{ \"name\": }\"sam doe\" }");
      }
    });

//    tests.add("Missing Quote", new Test() {
//      @Override
//      public void run(JSONFactory factory) throws Exception {
//        final JSONParser parser = factory.parser();
//        final JSON obj = parser.parse("{ \"name\": {\"sam doe\" }");
//      }
//    });

    tests.run(new JSONFactory(){
      @Override
      public JSONParser parser() {
        return new MyJSONParser();
      }

      @Override
      public JSON object() {
        return new MyJSON();
      }
    });
  }
}
