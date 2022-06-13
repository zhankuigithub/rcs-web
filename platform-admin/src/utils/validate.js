/**
 * Created by PanJiaChen on 16/11/18.
 */

/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validUsername(str) {
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

/**
 * 身份证有效性校验
 */
export const idCardValidity = (rule, code, callback) => {
  var city = { 11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古", 21: "辽宁", 22: "吉林", 23: "黑龙江 ", 31: "上海", 32: "江苏", 33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南", 42: "湖北 ", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆", 51: "四川", 52: "贵州", 53: "云南", 54: "西藏 ", 61: "陕西", 62: "甘肃", 63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外 " };
  var tip = ""
  var pass = true

  if (!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)) {
    tip = "身份证号格式错误"
    pass = false;
  } else if (!city[code.substr(0, 2)]) {//地址编码
    tip = "身份证号格式错误"
    pass = false
  } else {
    // 18位身份证需要验证最后一位校验位
    if (code.length === 18) {
      code = code.split('')
      // ∑(ai×Wi)(mod 11)
      // 加权因子
      var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
      // 校验位
      var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2]
      var sum = 0
      var ai = 0
      var wi = 0
      for (var i = 0; i < 17; i++) {
        ai = code[i]
        wi = factor[i]
        sum += ai * wi
      }
      var last = parity[sum % 11];
      if (parity[sum % 11] != code[17]) {//校验位
        tip = "身份证号格式错误"
        pass = false
      }
    }
  }
  if (!pass) {
    callback(new Error(tip))
  } else {
    callback()
  }
};

/**
 * 身份证长度校验
 * @param {*} rule 
 * @param {*} value 
 * @param {*} callback 
 */
export const idCard = (rule, value, callback) =>{
  if (value && (!(/\d{17}[\d|x]|\d{15}/).test(value) || (value.length !== 15 && value.length !== 18))) {
      callback(new Error('身份证号码不符合规范'))
  } else {
      callback()
  }
};

/**
 * 手机号码校验
 * @param {*} rule 
 * @param {*} value 
 * @param {*} callback 
 */
export const phoneNumber = (rule, value, callback)=> {
  if (value && (!(/^1[3|4|5|6|7|8|9][0-9]{9}$/).test(value) 
  && !(/^[1-9]\d*$/).test(value) 
  && !(/^400[0-9]{7}$/).test(value)
  && !(/^800[0-9]{7}$/).test(value)) 
  || value.length !== 11) {
      callback(new Error('手机号码不符合规范'))
  } else {
      callback()
  }
};

/**
     * 电话号码校验
     */
export const telephoneNumber = (rule, value, callback)=>{
  if (value && (!(/^((0\d{2,3}-\d{7,8})|(0\d{2,3}-\d{7,8}-\d{2,4})|(1[3584]\d{9}))$/).test(value))) {
      callback(new Error('电话号码不符合规范'))
  } else {
      callback()
  }
};

/**
 * 邮箱校验
 * @param {*} rule 
 * @param {*} value 
 * @param {*} callback 
 */
export const emailValue = (rule, value, callback) =>{
  let temp = /^[\w.\-]+@(?:[a-z0-9]+(?:-[a-z0-9]+)*\.)+[a-z]{2,3}$/
  let tempOne = /^[A-Za-zd]+([-_.][A-Za-zd]+)*@([A-Za-zd]+[-.])+[A-Za-zd]{2,5}$/
  if (value && (!(temp).test(value))) {
      callback(new Error('邮箱格式不符合规范'))
  } else {
      callback()
  }
};

/***
 * 网址校验
 */
export const urlString=(rule,value,callback)=>{
  let urlregex = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
  if(!urlregex.test(value)){
    callback(new Error('网址格式不正确'));
  }else{
    callback();
  }
};

/**
 * @description 校验密码是否小于6位
 * @param value
 * @returns {boolean}
 */
 export function isPassword(value) {
  return value.length >= 6
}

/**
 * 密码强度校验
*/
export function passwordStrengthCK(value){
  let mode = 0;
  //正则表达式验证符合要求的
  if (value.length < 1) return mode;
  if (/\d/.test(value)) mode++; //数字
  if (/[a-z]/.test(value)) mode++; //小写
  if (/[A-Z]/.test(value)) mode++; //大写
  if (/\W/.test(value)) mode++; //特殊字符
  return mode;
}

