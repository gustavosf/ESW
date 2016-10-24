class Conferencia < Element
  field :isbn, type: String
  field :editora, type: String
  field :volume, type: String
  field :fasciculo, type: String
  field :edicao, type: String
  field :pag_inicial, type: Integer
  field :pag_final, type: Integer
  field :pais, type: String
  field :autores, type: Array, default: []
end
