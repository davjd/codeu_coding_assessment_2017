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

final class MyJSONParser implements JSONParser {

  @Override
  public JSON parse(String in) throws IOException {
    boolean completed = true;
    boolean passedColon = false;
    String key = "";
    MyJSON map = new MyJSON();

    for(int i = 0, endStr = in.length(); i < endStr; ++i){
      char token = in.charAt(i);
      if(token == '"'){
        int end = in.indexOf(token, i + 1);
        if(completed){
          key = in.substring(i + 1, end);
          completed = false;
        }
        else{
          map.setString(key, in.substring(i + 1, end));
          passedColon = false;
          completed = true;
        }
        i = end;
      }
      else if(token == '{' && !completed){
        map.setObject(key, parse(in.substring(i + 1)));
        passedColon = false;
        completed = true;
        i += ((MyJSON)map.getObject(key)).getLen();
      }
      else if(token == '}' && completed && passedColon){
        map.setLen(++i);
        return map;
      } // there has to be a better way to take care of exceptions :(
      else if(token == ':'){
        if(passedColon){
          throw new IOException("Duplicate colon before given value.");
        }
        else{
          passedColon = true;
        }
      }
      else if(completed){
        if(passedColon){
          if(token != ',' && token != ' ' && token != '}'){
            System.out.println(token + " is illegal.");
            throw new IOException("Invalid character after given key.");
          }
        }
        else{
          if(token == '}'){

          }
        }
      }
      else if(token != ' ' && (token != '{' && !completed)){
        System.out.println(token + " is illegal.");
        throw new IOException("Invalid character after given key.");
      }
    }
    return map;
  }
}
