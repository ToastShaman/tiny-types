package com.github.toastshaman.tinytypes.http.domain;

import org.json.JSONObject;

public record Todo(int userid, int id, String title, boolean completed) {

    public static class Json {

        private Json() {
            // Prevent instantiation
        }

        public static Todo fromJson(String json) {
            var object = new JSONObject(json);
            var userId = object.getInt("userId");
            var id = object.getInt("id");
            var title = object.getString("title");
            var completed = object.getBoolean("completed");
            return new Todo(userId, id, title, completed);
        }
    }
}
