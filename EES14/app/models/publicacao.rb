class Publicacao
  include Mongoid::Document
  include Mongoid::Attributes::Dynamic

  field :_type

  field :ano, type: Integer
  field :titulo, type: String
  field :idioma, type: String
  field :link, type: String
end
