package com.github.toastshaman.tinytypes.http;

import org.json.JSONObject;

public record Todo(int userid, int id, String title, boolean completed) {

    public static class Json {
        public static Todo fromJson(String json) {
            var jsonObject = new JSONObject(json);
            return new Todo(
                    jsonObject.getInt("userId"),
                    jsonObject.getInt("id"),
                    jsonObject.getString("title"),
                    jsonObject.getBoolean("completed"));
        }
    }
}
