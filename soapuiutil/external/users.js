// users.js
module.exports = function() {
  var roles = ["ADMIN", "READER", "WRITER", "AUTHOR"]
  var data = { users: [] }
  // Create 12 users
  for (var i = 0; i < 12; i++) {
var roleindex = i % 4
//data.users.push({ id: i, name: 'user' + i, roles: [{role1:roles[roleindex]},{role2:roles[roleindex+1]}] })
    data.users.push({ id: i, name: 'user' + i, role:roles[roleindex] })
  }
  return data
}