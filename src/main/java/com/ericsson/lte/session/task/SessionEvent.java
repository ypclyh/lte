package com.ericsson.lte.session.task;

import com.ericsson.lte.session.entity.RequestBean;
import com.ericsson.lte.session.entity.ResponseBean;
import seesionClient.SocketClient;
import com.ericsson.lte.session.controller.SessionController;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class SessionEvent implements Callable<ResponseBean> {
    private final static Logger logger = Logger.getLogger(SessionController.class.getName());
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private SocketClient socketClient;
    private RequestBean requestBean;

    {
        try {
            socketClient = new SocketClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SessionEvent(RequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public ResponseBean call() {
        try {
            objectOutputStream.writeObject(requestBean);
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(new BufferedInputStream(socketClient.getInputStream()));
            Object obj = objectInputStream.readObject();
            if (obj != null) {
                ResponseBean res = (ResponseBean) obj;
                logger.info("ResponseBean:" + res.getUrl() + "/" + res.getConnectionState());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(objectInputStream, objectOutputStream);
        }
        return new ResponseBean(0, requestBean.getStartTime(),requestBean.getStopTime(), requestBean.getUrl());
    }

    private void close(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        if (objectInputStream != null) {
            try {
                objectInputStream.close();
            } catch (IOException ex) {
                logger.warning("close the objectInputStream fail." + ex);
            }
        }
        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException ex) {
                logger.warning("close the objectOutputStream fail." + ex);
            }
        }
    }
}
