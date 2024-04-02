const routes = {
    HOME: '/',
    otherPage: {
        ERROR: '*',
    },
    auth: {
        LOGIN: '/login',
        SIGNUP: '/signup',
        FORGETPASSWORD: '/forgetpassword',
        VERIFYREGISTER: '/VerifyRegister',
        CREATENEWPASSWORD: '/createnewpassword',
    },
    translation: {
        TRANSLATION: '/translation',
    },
    wordbooks: {
        WORDBOOK: '/wordbooks',
        WORDBOOKS: '/wordbooks/:categoryName/:page',
        WORDINFOLDER: '/wordbooks/:categoryId/:idFolder/:pageWord',
    },
    video: {
        VIDEO: '/video',
        VIDEOS: '/video/:page',
        VIDEODEDAIL: '/video/:page/:idVideo',
    },
    news: {
        NEW: '/news',
        NEWS: '/news/:page',
        NEWDEDAIL: '/news/:page/:idNew',
    },
    text_online: {
        TEXT_ONLINE: '/text_online',
    },
    forum: {
        FORUM: '/forum',
        MYPOST: '/forum/my_post',
        COMUNITY: '/forum/comunity',
    },
    chat_ai: {
        CHAT_AI: '/chat_ai',
    },
    game: {
        GAME: '/game',
    },
    lookup: {
        LOOKUP: '/lookup/:word',
    },
    admin: {
        MANAGEUSER: '/manage_user/:page',
        MANAGEFORUM: '/manage_forum',
        MANAGEQUIZZES: '/manage_quizzes',
        MANAGEWORDCATEGORIES: '/manage_wcategories/:page',
        MANAGEWORDINCATEGORY: '/manage_wcategories/:cagtegoryId/:page',
        MANAGENEWSINCATEGORY: '/manage_news/:categoryId/:page',
        MANAGEVIDEOINCATEGORY: '/manage_videos/:categoryId/:page',
        MANAGENEWS: '/manage_news/:page',
        MANAGEVIDEOS: '/manage_videos/:page',
    },
};

export default routes;
