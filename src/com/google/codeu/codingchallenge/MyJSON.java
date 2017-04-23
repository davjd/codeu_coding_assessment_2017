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

import java.util.Collection;
import java.util.HashMap;

final class MyJSON implements JSON {

  // variable that will hold the key-value pairs in the memory.
  // the reason its generic is because the
  // the value might either be a String or a JSON object.
  private HashMap<String, Object> mem;
  private int objectLen;

  public MyJSON(){
    mem = new HashMap<String, Object>();
    objectLen = -1;
  }

  @Override
  public JSON getObject(String name) {
    if(mem.get(name) instanceof JSON){
      return (JSON)mem.get(name);
    }
    return null;
  }

  @Override
  public JSON setObject(String name, JSON value) {
    mem.put(name, value);
    return this;
  }

  @Override
  public String getString(String name) {
    if(mem.get(name) instanceof String){
      return (String)mem.get(name);
    }
    return null;
  }

  @Override
  public JSON setString(String name, String value) {
    mem.put(name, value);
    return this;
  }

  @Override
  public void getObjects(Collection<String> names) {
    for (HashMap.Entry<String, Object> it : mem.entrySet())
    {
      if(it.getValue() instanceof JSON){
        names.add(it.getKey());
      }
    }
  }

  @Override
  public void getStrings(Collection<String> names) {
    for (HashMap.Entry<String, Object> it : mem.entrySet())
    {
      if(it.getValue() instanceof String){
        names.add(it.getKey());
      }
    }
  }

  public void setLen(int l){
    objectLen = l;
  }

  public int getLen(){
    return objectLen;
  }

  public HashMap<String, Object> getMem(){
    return mem;
  }
}
