function isPoneAvailable(tel) {
    var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(tel)) {
        return false;
    } else {
        return true;
    }
}