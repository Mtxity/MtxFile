# MtxFile
File reader, parser, jsonifier, and generator API

## Read Endpoints
All endpoints listen for POST requests and take in a multipart form body with one file (under key: `file`).
* `/read/contents` - Reads contents of file and returns filename, metadata, and file contents.
* `/read/jsonify/csv` - Reads a CSV file and returns JSON array of objects. Each object represents a row. Headers are the keys.
* `/read/jsonify/xls` - Reads an Excel file and returns JSON array of objects. Each object represents a row. Headers are the keys. Multiple sheets will split up results into a parent array.
* `/read/jsonify/xml` - Reads an XML file and returns a JSON representation.
* `/read/jsonify/yml` - Reads a YAML file and returns a JSON representation.

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