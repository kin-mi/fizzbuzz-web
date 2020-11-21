# fizzbuzz-web
FizzBuzzする為だけのWEB

## FizzBuzz API
https://asia-northeast1-fizzbuzz-web.cloudfunctions.net/fizzbuzz-backend

### **Request**

Only GET Method.
|  パラメータ名  | 必須 | 設定値  |  最大値  |
| ---- | :---: | ---- | ---- |
|  start  | ○ |  FizzBuzzする値  | 2,147,483,647 |
|  range  |  |  startから連続して取得する数  | 1,000 |

e.g.) https://asia-northeast1-fizzbuzz-web.cloudfunctions.net/fizzbuzz-backend?start=1&range=15

### **Response Body**

JSON Style.
|  パラメータ名  | 型 | 設定値  | 値 |
| ---- | ---- | ---- | ---- |
|  success  | Boolean |  処理結果  | true |
|  data  | Object[] |  number: 処理対象の値<br>fizzbuzz: FizzBuzz結果  | [{<br>"number": 4,<br>"fizzbuzz": "4"<br>},<br>{<br>"number": 5,<br>"fizzbuzz": "Buzz"<br>}] |

### Run locally
```
cd fizzbuzz-web/back-end/

mvn function:run
```
http://localhost:8080/

### Deploy to Cloud Functions
```
PROJECT_NAME=GCPプロジェクト名
FUNCTION_NAME=関数名

gcloud functions deploy ${FUNCTION_NAME} --entry-point backend.service.FizzBuzz --project ${PROJECT_NAME} --region=asia-northeast1 --runtime java11 --trigger-http --memory 512MB --allow-unauthenticated

curl -X GET https://asia-northeast1-${PROJECT_NAME}.cloudfunctions.net/${FUNCTION_NAME}?start=15
```
