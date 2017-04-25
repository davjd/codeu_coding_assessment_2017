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
    System.out.println("ALL TEST MUST PASS, UNLESS AN EXCEPTION WAS MEANT TO BE INVOKED.");
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
        final JSON obj = parser.parse("{ \"name\":\"sam doe\" } ");

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
        final JSON obj = parser.parse("{ \"{google}\":\"is mad lit.\" }");

        Asserts.isEqual("is mad lit.", obj.getString("{google}"));
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

    tests.add("Depths of Hell", new Test() { // lol
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{\"r\":{\"e\":{\"c\":{\"u\":{\"r\":{\"s\":{\"i\":{\"o\":{\"n\":{\"g\":{\"a\":{\"n\":{\"g\":\"functional programming <33333333333\"}}}}}}}}}}");
        final JSON end = obj.getObject("r").getObject("e").getObject("c").getObject("u").getObject("r").getObject("s").getObject("i").getObject("o").getObject("n").getObject("g").getObject("a").getObject("n");
        Asserts.isEqual("functional programming <33333333333", end.getString("g"));
      }
    });


    tests.add("String Object Mixed", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"kid,\":\"cudi}\", \"hob-bies\" : { \"da'y\" : \"something\", \"night\" : \"else\"}, \"uh\" : \"nuh\"}");

        final JSON hobbies = obj.getObject("hob-bies");

        Asserts.isEqual("cudi}", obj.getString("kid,"));
        Asserts.isEqual("something", hobbies.getString("da'y"));
        Asserts.isEqual("else", hobbies.getString("night"));
        Asserts.isEqual("nuh", obj.getString("uh"));
      }
    });


    tests.add("No Whitespace", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{\"uh\":{\"uh\":\"uh\",\"yuh\":\"yuh\"}}");
        final JSON inner = obj.getObject("uh");

        Asserts.isEqual("uh", inner.getString("uh"));
        Asserts.isEqual("yuh", inner.getString("yuh"));
      }
    });

    tests.add("Unique Keys", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"uh\":\"first\", \"uh\" : \"second\"}");

        Asserts.isEqual("second", obj.getString("uh"));
      }
    });

    tests.add("Valid Escape Characters", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"u\\th\\n\":\"first\", \"u\\th\\n\" : \"second\"}");

        Asserts.isEqual("second", obj.getString("u\\th\\n"));
      }
    });

    tests.add("Empty Object Key", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"anothaa one\": {}} ");
        final JSON empty = obj.getObject("anothaa one");

        Asserts.isNotNull(empty);
      }
    });

    /*
    * Testing for invalid JSON-Lite schemas.
    * All of the following tests SHOULD fail,
    * because of exceptions thrown.
    * */

    tests.add("(IOException) Illegal character after given value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '}' after value should not be there = throw error.
        final JSON obj = parser.parse("{ \"name\": \"sam doe\" };");
      }
    });

    tests.add("(IOException) Illegal character after given key", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '}' after colon should not be there = throw error.
        final JSON obj = parser.parse("{ \"name\": }\"sam doe\" }");
      }
    });

    tests.add("(IOException) Illegal Beginning of Schema", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // ','  should not be there = throw error.
        final JSON obj = parser.parse(",{ \"cant\": \"catch thiss\" }");
      }
    });

    tests.add("(IOException) Missing Key", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '{'  should not be there = throw error.
        final JSON obj = parser.parse("{{ \"name\": \"sam doe\" } ");
      }
    });

    tests.add("(IOException) Incomplete Schema", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '}'  missing = throw error.
        final JSON obj = parser.parse("{\"uhhh\": \"uhhhhh\" ");
      }
    });

    tests.add("(IOException) Illegal Escape Character", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        // '\g'  should not be there = throw error.
        final JSON obj = parser.parse("{\"\\g\": \"uhhhhh\" ");
      }
    });

    tests.add("(IOException) Incomplete Schema(2)", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{\"\" ");
      }
    });

    tests.add("(IOException) Invalid Input", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse(" pleasedont : \"work\"");
      }
    });

    tests.add("(IOException) Illegal Key", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"anothaa one\": [}} ");
      }
    });

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
