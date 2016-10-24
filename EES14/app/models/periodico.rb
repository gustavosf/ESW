class Periodico < Element
  field :issn, type: String
  field :doi, type: String
  field :volume, type: String, default: nil
  field :fasciculo, type: String, default: nil
  field :edicao, type: String, default: nil
  field :pag_inicial, type: Integer, default: nil
  field :pag_final, type: Integer, default: nil
  field :pais, type: String
  field :autores, type: Array, default: []
end