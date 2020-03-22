package com.ericsson.lte.session.controller;

import com.ericsson.lte.session.consts.ParametersConstants;
import com.ericsson.lte.session.entity.RequestBean;
import com.ericsson.lte.session.entity.ResponseBean;
import com.ericsson.lte.session.entity.ResponseResult;
import com.ericsson.lte.session.exception.SessionControlException;
import com.ericsson.lte.session.task.SessionEvent;
import com.ericsson.lte.session.utils.SessionEventPool;
import com.ericsson.lte.session.utils.FileUtils;
import com.ericsson.lte.session.utils.RedisUtil;
import org.testng.collections.Lists;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class SessionController {
    private final static Logger logger = Logger.getLogger(SessionController.class.getName());

    public static void handleRequestTask(ExecutorService executor) {
        long start = System.currentTimeMillis();
        CompletableFuture[] cfArr = getTaskRequest().stream().
                map(task -> CompletableFuture
                        .supplyAsync(() -> dealSessionRequest(task), executor)
                        .whenComplete((result, th) -> FileUtils.writeSessionToFile(result)
                        )).toArray(CompletableFuture[]::new);
        // 开始等待所有任务执行完成
        logger.info("start block");
        CompletableFuture.allOf(cfArr).join();
        logger.info("block finish, consume time:" + (System.currentTimeMillis() - start));
    }

    private static ResponseResult dealSessionRequest(RequestBean requestBean) {
        try {
            check(requestBean);
        } catch (SessionControlException e) {
            e.printStackTrace();
        }
        ResponseBean responseBean = null;
        if (Objects.requireNonNull(RedisUtil.getJedis()).get(requestBean.getDeliverySessionId()) == null) {
            requestBean.setActionType(ParametersConstants.STOP);
        }
        SessionEvent sessionEvent = new SessionEvent(requestBean);
        Future<ResponseBean> future = SessionEventPool.getTaskPool().submit(sessionEvent);
        try {
            responseBean = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ResponseResult().code(0).message(ParametersConstants.OK).responseBean(responseBean);
    }

    private static void check(RequestBean requestBean) throws SessionControlException {
        //检测参数的有效性
        if (requestBean==null){
            throw new SessionControlException(ParametersConstants.REQUEST_BEAN_INVALID);
        }
    }

    private static List<RequestBean> getTaskRequest() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the address:");
        String s = sc.next();//
        File file = new File(s);
        //将file 文件解析为javaBean 并封装为requestBeanList
        List<RequestBean> requestBeanList = Lists.newArrayList();
        for (RequestBean param : requestBeanList) {
            Objects.requireNonNull(RedisUtil.getJedis()).expire(param.getDeliverySessionId(), Integer.parseInt(param.getStopTime()));
        }
        return requestBeanList;
    }
}
