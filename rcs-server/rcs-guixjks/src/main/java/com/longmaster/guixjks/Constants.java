package com.longmaster.guixjks;

public interface Constants {

    interface Server {
        String FS_AB_PATH = "/data/rcs/fs/guijk/image/";//fs服务器绝对地址
        String FS_FFMPEG_CMD = "yes | /usr/local/bin/ffmpeg -i /data/rcs/fs/guijk/image/{0}.amr /data/rcs/fs/guijk/image/{0}.mp3";//mp3转换命令行
        String FS_DOWNLOAD_URL = "https://rcs-guijk.langma.cn/image/"; //mapp下载来的字眼地址
        String FS_FFMPEG_CROP_IMG = "yes | /usr/local/bin/ffmpeg -i /data/rcs/fs/guijk/image/{0}.{1} -ss 1 -f image2 /data/rcs/fs/guijk/image/{0}.jpg";//视频获取封面图片命令行
    }

    interface Tip {
        String NOT_DEVELOP = "功能暂时没有开放!";
        String BR = "\r\n";   //换行分隔符
        String TestQuestionAnswer_delimiter = "##";

        //欢迎
        String WELCOME = "你好";
        String WELCOME_REPLY = "欢迎使用贵健康5G消息服务，贵州省内预约挂号、名医专家在线问诊，还可提供健康自测、健康资讯服务及各种会员优惠活动等。";

        String SEE_DOCTOR_SEX = "Hi～我是您的智能导诊小助手，很高兴为您服务。您可以告诉我您的症状或疾病，我将为您找到适合的医生，请选择您的性别";
        String SEE_DOCTOR_AGE = "请选择您的年龄";
        String SEE_DOCTOR_PREGNANT = "请问您是否怀孕？";

        String TIP_HISTORY_RECORD = "请选择下方按钮查看相应记录。";
        String TIP_MENU_HELP = "我的您的健康小助手,请选择下方按钮选择需要帮助的内容~~";
        String TIP_MENU_HELP_FEEDBACK = "请通过下方输入框告诉我们您的宝贵建议和意见吧！还可添加最多3张图片(如上传图片超过3张，将以最后提交的3张为准)，上传完成后记得点击“提交”哟~~";

        String TIP_FEEDBACK_SUCCESS = "上传完成后记得点击“提交”哟~~";
        String TIP_FEEDBACK_ERROR = "只能上传图片哦~~";
        String TIP_FEEDBACK_COMPLETE = "您的建议意见已经提交成功，将有工作人员在第一时间查看，如有必要将有与您联系，感谢您对我们的支持和理解~";

    }

    //场景相关的常量配置
    interface Scene {
        String EN_NAME = "en_name"; //英文
        String NOT_DEVELOP = "set_by_chatbot_not_develop";//功能暂时未开放,回复用户文字信息
        String NO_RESPONSE = "set_by_chatbot_no_response";//无需相应的消息,如跳转到h5页面时候
        String EN_WELCOME = "set_by_chatbot_welcome";//首次进入场景
        String EN_TEST_GUIJK_LINK = "test_guijk_link";

        String MAKE_APPOINTMENT = "sbc_menu_hosp_register";
        String ONLINE_ASK = "sbc_menu_ask_doctor";
        String FAMILY_ARCHIVES = "sbc_menu_patient";
        String HISTORY_RECORD = "sbc_menu_record";
        String MENU_HELP = "sbc_menu_help";
        String MENU_HELP_FEEDBACK = "sbc_menu_help_feedback";
        String MENU_HELP_FEEDBACK_SUBMIT = "sbc_menu_help_feedback_submit";

        String HEALTH_INFORMATION = "sbc_health_information";
        String HEALTH_INFORMATION_SIMULATION = "sbc_health_information_simulation";
        String HEALTH_INFORMATION_NEXT = "sbc_health_information_next"; // 健康资讯换一批
        String HEALTH_INFORMATION_VIDEO = "sbc_health_information_video"; // 健康资讯的视频
        String HEALTH_INFORMATION_VIDEO_SIMULATION = "sbc_health_information_video_simulation"; // 健康资讯的视频(模拟)
        String HEALTH_INFORMATION_VIDEO_NEXT = "sbc_health_information_video_next"; // 健康资讯的视频 下一页

        String CUSTOMER_SERVICE = "sbc_menu_help_customer_service";
        String HEALTH_VIDEO = "sbc_health_video";
        String GUIXJK_RETURN_HOME = "sbc_guixjk_return_home"; // 贵象健康统一返回入口

        String PATIENT_LIST = "sbc_patient_list"; // 家庭档案，成员
        String MESSAGES_LIST = "sbc_messages_list"; // 未读消息
        String VOUCHER_LIST = "sbc_voucher_list"; // 问医券

        // 搜索医生
        String SBC_SEARCH_DOCTOR = "sbc_search_doctor";
        String SBC_SEARCH_DOCTOR_NEXT = "sbc_search_doctor_next";

        // 挂号记录
        String SBC_REGISTRATION_RECORD = "sbc_registration_record";
        String SBC_REGISTRATION_RECORD_NEXT = "sbc_registration_record_next";

        // 问诊记录
        String SBC_DIAGNOSIS_DOCTOR = "sbc_diagnosis_doctor";
        String SBC_DIAGNOSIS_DOCTOR_NEXT = "sbc_diagnosis_doctor_next";

        interface Menu {
            String PROPERTY_MENU = "menu";//属性
            String PROPERTY_TYPE = "type";//熟悉
            String REPLY_MANY_CARD = "reply_many_card"; //带建议的多卡片
            String REPLY_SINGLE_CARD = "reply_single_card"; //带建议的单卡片
            String REPLY_TEXT = "reply_text"; //带建议的文本
        }
    }

    interface Maap {
        String TYPE_REPLY = "application/vnd.gsma.botsuggestion.response.v1.0+json";//基于建议回复消息的回复
        String TYPE_SHARED = "application/vnd.gsma.botsharedclientdata.v1.0+json";//终端共享配置数据
        String TYPE_FILE = "application/vnd.gsma.rcs-ft-http+xml";//上传文件，图片等信息

        interface FileDownloadStatus {   //文件下载状态
            int INIT = 0; //文件等待下载
            int START = 1; //开始下载
            int SUCCESS = 2;//下载成功
            int FAIL = -1; //下载失败
        }

        interface PostBack {
            String RESPONSE = "response";
            String REPLY = "reply";
            String POST_BACK = "postback";
            String POST_BACK_TEXT = "displayText";
            String DATA = "data";
            String ACTION = "action";
        }
    }


    // 智能导诊相关的操作以sbc_menu_guidance开头
    interface ToSeeDoctor {

        interface Patient {
            String MALE = "male";
            String FEMALE = "female";
        }

        String EN_SEE_DOCTOR = "sbc_menu_guidance";
        String EN_SEE_DOCTOR_SEX_MAN = "sbc_menu_guidance_man";
        String EN_SEE_DOCTOR_SEX_WOMAN = "sbc_menu_guidance_woman";

        String EN_SEE_DOCTOR_AGE_1 = "sbc_menu_guidance_age_1";
        String EN_SEE_DOCTOR_AGE_2 = "sbc_menu_guidance_age_2";
        String EN_SEE_DOCTOR_AGE_3 = "sbc_menu_guidance_age_3";
        String EN_SEE_DOCTOR_AGE_4 = "sbc_menu_guidance_age_4";
        String EN_SEE_DOCTOR_AGE_5 = "sbc_menu_guidance_age_5";
        String EN_SEE_DOCTOR_AGE_6 = "sbc_menu_guidance_age_6";

        String EN_SEE_DOCTOR_PREGNANT_1 = "sbc_menu_guidance_pregnant_1";
        String EN_SEE_DOCTOR_PREGNANT_0 = "sbc_menu_guidance_pregnant_0";

        String SBC_MENU_GUIDANCE_RECOMMEND = "sbc_menu_guidance_recommend";
        String SBC_MENU_GUIDANCE_SYMPTOMS = "sbc_menu_guidance_symptoms";


        interface Dynamic {
            String SBC_MENU_GUIDANCE_CHAT = "sbc_menu_guidance_chat_";
            String SBC_MENU_GUIDANCE_ASK = "sbc_menu_guidance_go_ask_";
        }

    }

    // rcs-csp的
    interface Csp {
        String MESSAGE = "api/message/receiveCustom"; // 向csp发消息体
        String GET_MATERIAL_URL = "api/material/getAuditMaterialUrl"; // 获取审核过的素材
        String GET_MATERIAL_URL_BY_ORIGINAL = "api/material/getAuditMaterialUrlByOriginalUrl"; // 获取审核过的素材
        String MESSAGE_MSG_TMP = "api/message/sendMessageByMsgTmpId"; // 通过消息模板id，向csp发
    }
}
