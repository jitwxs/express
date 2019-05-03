// 是否合法手机号
function isPoneAvailable(tel) {
    var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(tel)) {
        return false;
    } else {
        return true;
    }
}

// 是否正整数
function isPositiveInteger(s){//是否为正整数
    let re = /^[0-9]+$/;
    return re.test(s)
}

//添加cookie
function addCookie(name, value, expiresHours) {
    let cookieString = name + "=" + escape(value);
    //判断是否设置过期时间,0代表关闭浏览器时失效
    if (expiresHours > 0) {
        let date = new Date();
        date.setTime(date.getTime() + expiresHours * 1000);
        cookieString = cookieString + ";expires=" + date.toUTCString();
    }
    document.cookie = cookieString;
}

//修改cookie的值
function editCookie(name, value, expiresHours) {
    let cookieString = name + "=" + escape(value);
    if (expiresHours > 0) {
        let date = new Date();
        date.setTime(date.getTime() + expiresHours * 1000); //单位是毫秒
        cookieString = cookieString + ";expires=" + date.toGMTString();
    }
    document.cookie = cookieString;
}

//根据名字获取cookie的值
function getCookieValue(name) {
    let strCookie = document.cookie;
    let arrCookie = strCookie.split(";");
    for (let i = 0; i < arrCookie.length; i++) {
        let arr = arrCookie[i].split("=");
        if (arr[0] === name) {
            return unescape(arr[1]);
        } else {
            return "";
        }
    }
}
