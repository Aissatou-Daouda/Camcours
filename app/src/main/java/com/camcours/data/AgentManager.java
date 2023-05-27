package com.camcours.data;

import android.content.SharedPreferences;

import com.camcours.backend.Client;

import org.json.JSONException;
import org.json.JSONObject;

public class AgentManager {
    private JSONObject agent;
    private SharedPreferences preferences;
    private static AgentManager self;

    public interface AuthenticationListener {
        void agentAuthenticated(JSONObject agent);
        void agentAuthenticationFailed(JSONObject agent);
    }

    public interface GetListener {
        void agentFound(JSONObject agent);
        void agentNotFound(int agentId);
    }

    public interface RegistrationListener {
        void agentAdded(JSONObject agent);
        void agentAddFailed(JSONObject error);
    }

    public interface UpdateListener {
        void agentUpdated(JSONObject agent);
        void agentUpdateFailed(JSONObject error);
    }

    public AgentManager(SharedPreferences preferences) {
        this.agent = null;
        this.preferences = preferences;
        self = this;
    }

    public static AgentManager getInstance() {
        return self;
    }

    public JSONObject getAgent() {
        return agent;
    }

    public void forgetAgent() {
        agent = null;
        saveData();
    }

    public void authenticateAgent(String login, String password, AuthenticationListener listener) {
        JSONObject agent = new JSONObject();
        try {
            agent.put("login", login);
            agent.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Client.OperationListener l = new Client.OperationListener() {
            @Override
            public void operationRunning(long processed, long total) {
                //
            }

            @Override
            public void operationFinished(int httpStatusCode, JSONObject response) {
                AgentManager.this.agent = response;
                saveData();
                listener.agentAuthenticated(response);
            }

            @Override
            public void operationFailed(Exception e) {
                listener.agentAuthenticationFailed(agent);
                e.printStackTrace();
            }
        };

        Client.getInstance().post("/agent/login", null, agent, l);
    }

    public void authenticateAgent(int agentId, AuthenticationListener listener) {
        getAgent(agentId, new GetListener() {
            @Override
            public void agentFound(JSONObject agent) {
                AgentManager.this.agent = agent;
                saveData();
                listener.agentAuthenticated(agent);
            }

            @Override
            public void agentNotFound(int agentId) {
                JSONObject agent = new JSONObject();
                try {
                    agent.put("id", agentId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.agentAuthenticationFailed(agent);
            }
        });
    }

    public void getAgent(int agentId, GetListener listener) {
        Client.getInstance().get("/agent/" + agentId, null, new Client.OperationListener() {
            @Override
            public void operationRunning(long processed, long total) {
                //
            }

            @Override
            public void operationFinished(int httpStatusCode, JSONObject response) {
                if (httpStatusCode == 200)
                    listener.agentFound(response);
                else
                    listener.agentNotFound(agentId);
            }

            @Override
            public void operationFailed(Exception exception) {
                listener.agentNotFound(agentId);
            }
        });
    }

    public void updateAgent(JSONObject agent, UpdateListener listener) {
        try {
            Client.getInstance().put("/agent/" + agent.getInt("id"), null, agent, new Client.OperationListener() {
                @Override
                public void operationRunning(long processed, long total) {
                    //
                }

                @Override
                public void operationFinished(int httpStatusCode, JSONObject response) {
                    if (httpStatusCode == 200)
                        listener.agentUpdated(response);
                    else
                        listener.agentUpdateFailed(response);
                }

                @Override
                public void operationFailed(Exception exception) {
                    listener.agentUpdated(agent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registerAgent(JSONObject agent, JSONObject school, RegistrationListener listener) {
        Client.getInstance().post("/school", null, school, new Client.OperationListener() {
            @Override
            public void operationRunning(long processed, long total) {
                //
            }

            @Override
            public void operationFinished(int httpStatusCode, JSONObject response) {
                try {
                    if (httpStatusCode == 201 || httpStatusCode == 200) {
                        agent.put("school", response.getInt("id"));
                        registerAgent(agent, true, listener);
                    } else
                        listener.agentAddFailed(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void operationFailed(Exception exception) {
                listener.agentAddFailed(null);
            }
        });
    }

    public void registerAgent(JSONObject agent, boolean authenticate, RegistrationListener listener) {
        Client.OperationListener l = new Client.OperationListener() {
            @Override
            public void operationRunning(long processed, long total) {
                //
            }

            @Override
            public void operationFinished(int httpStatusCode, JSONObject response) {
                if (httpStatusCode == 201 || httpStatusCode == 200) {
                    if (authenticate) {
                        AgentManager.this.agent = response;
                        saveData();
                    }
                    listener.agentAdded(response);
                } else
                    listener.agentAddFailed(response);
            }

            @Override
            public void operationFailed(Exception e) {
                listener.agentAddFailed(null);
            }
        };

        Client.getInstance().post("/agent", null, agent, l);
    }

    public void loadData(AuthenticationListener listener) {
        if (preferences.contains("agentId"))
            authenticateAgent(preferences.getInt("agentId", 0), listener);
    }

    public void saveData() {
        SharedPreferences.Editor edit = preferences.edit();

        if (agent != null) {
            try {
                edit.putInt("agentId", agent.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (preferences.contains("agentId")) {
            edit.remove("agentId");
        }

        edit.apply();
    }
}
