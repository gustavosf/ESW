# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).

User.delete_all
users = JSON.parse(File.read(Rails.root.join('db/seeds/users.json')))
users.each do |user|
  User.create!(user)
end
