package br.com.victor.provapratica.servico;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.victor.provapratica.model.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Victor Oliveira on 09/08/18.
 * SINFO - UFRN
 * victorlopejg@info.ufrn.com
 */
public class ServicoApp {

    private static ObjectMapper objectMapper = null;

    // URL base dos serviços.
    private final static String URL_BASE = "https://dev.people.com.ai/mobile/api/v2/";
    // Content type padrão para o uso dos serviços
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Esse método serve para o registro do usuário no servidor
    public User register(String nome, String email, String password) throws Exception {

        User user = new User(nome, email, password);
        String json = getObjectMapperInstance().writeValueAsString(user);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(URL_BASE)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return getObjectMapperInstance().readValue(response.body().string(), User.class);
        } else {
            throw new Exception(tratarErro(response.code()));
        }
    }

    public String login(String email, String password) throws Exception {

        User user = new User(email, password);
        String json = getObjectMapperInstance().writeValueAsString(user);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(URL_BASE)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            JsonNode token = getObjectMapperInstance().readValue(response.body().string(), JsonNode.class);
            return token.get("token").textValue();
        } else {
            throw new Exception(tratarErro(response.code()));
        }
    }

    private String tratarErro(int code) {
        switch (code) {
            case 409:
                return "Esse usuário já está cadastrado em nossa base de dados.";
            case 403:
                return "E-mail ou Senha inválido!";
            case 400:
                return "Por favor insira todos os campos";
            default:
                return "Dados não encontrados";
        }
    }

    private static ObjectMapper getObjectMapperInstance() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }
}