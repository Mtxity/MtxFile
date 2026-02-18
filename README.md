# MtxFile
File reader, storer, parser, jsonifier, and generator API

# Read Endpoints
All endpoints listen for POST requests and take in a multipart form body with one file (under key: `file`).
* `/read/contents` - Reads contents of file and returns filename, metadata, and file contents.
* `/read/jsonify/csv` - Reads a CSV file and returns JSON array of objects. Each object represents a row. Headers are the keys.
* `/read/jsonify/xls` - Reads an Excel file and returns JSON array of objects. Each object represents a row. Headers are the keys. Multiple sheets will split up results into a parent array.
* `/read/jsonify/xml` - Reads an XML file and returns a JSON representation.
* `/read/jsonify/yml` - Reads a YAML file and returns a JSON representation.
* `/read/wordCount` - Reads a file and returns a count of words. Supports txt, md, pdf, csv, xls, xml and yml.
* `/read/summarize` - Reads a file and returns a one-paragraph summary. Supports txt, md, and pdf.
* `/read/hash` - Reads a file and returns a hash. Supports txt, md, and pdf. See supported hashing algorithms [here](https://github.com/Mtxity/MtxFile/blob/main/src/main/java/com/mtxrii/file/mtxfile/api/model/enumeration/HashType.java).

## Examples

### CSV
Input:
```csv
header1, header2,   header3
val1,    detail2,   detail3
val2,    detail4,   detail5
val3,    detail6,   detail7
```
Output:
```json
[
  {
    "header1": "val1",
    "header2": "detail2",
    "header3": "detail3"
  },
  {
    "header1": "val2",
    "header2": "detail4",
    "header3": "detail5"
  },
  {
    "header1": "val3",
    "header2": "detail6",
    "header3": "detail7"
  }
]
```

### XML
Input:
```xml
<user id="123">
  <name>Alice</name>
  <email>alice@test.com</email>
</user>
```
Output:
```json
{
  "id": "123",
  "name": "Alice",
  "email": "alice@test.com"
}
```

### YAML
Input:
```yaml
user:
  id: 123
  name: Alice
  email: alice@test.com
  active: true
  roles:
    - admin
    - user
  abilities:
    - name: ringUp
      action: "charge customer"
      category: pos
    - name: cleanUp
      action: "Restore layout"
      category: tos
```
Output:
```json
{
  "user": {
    "id": 123,
    "name": "Alice",
    "email": "alice@test.com",
    "active": true,
    "roles": [
      "admin",
      "user"
    ],
    "abilities": [
      {
        "name": "ringUp",
        "action": "charge customer",
        "category": "pos"
      },
      {
        "name": "cleanUp",
        "action": "Restore layout",
        "category": "tos"
      }
    ]
  }
}
```


# Upload Endpoints
* `/upload/contents` - Uploads a file and stores it by its filename. Accepts a password as a query parameter.
* `/upload/contents/{fileName}` - Retrieves a file by its filename. Requires a password as a query parameter if one was provided when uploading.
* `/upload/download/{fileName}` - Downloads a file by its filename. Requires a password as a query parameter if one was provided when uploading.
<!-- @TODO: Add /upload/delete/{fileName} endpoint -->


# Csv Endpoints
* `/csv/analytics/descript` - Reads a CSV file and returns descriptive statistics. Includes total rows, columns, empty values, unique values per header, and most common values per header.
* `/csv/analytics/frequencies` - Reads a CSV file and returns frequency counts for each unique value in each column.


# Hash Endpoints
This exposes the API used for hashing passwords used in the `/upload/contents` endpoints.
* `/hashing/hash` - Takes in a string as a query parameter and returns a hashed version of it.
* `/hashing/verify` - Takes in a string and a hashed string as query parameters and returns true if they match.