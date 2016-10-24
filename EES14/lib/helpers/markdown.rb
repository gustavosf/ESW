class MarkdownRendererWithSpecialLinks < Redcarpet::Render::HTML

  def autolink(link, link_type)
    case link_type
      when :url then url_link(link)
      when :email then email_link(link)
    end
  end

  def url_link(link)
    case link
      when /^http:\/\/youtube/ then youtube_link(link)
      when /^http:\/\/soundcloud/ then soundcloud_link(link)
      when /^http:\/\/mixcloud/ then mixcloud_link(link)
      else normal_link(link)
    end
  end

  # youtube://<id>
  def youtube_link(link)
    resource_id = link.match(/\/\/.*?\/(.*)$/)[1]
    "<iframe width=\"560\" height=\"315\" src=\"//www.youtube.com/embed/#{resource_id}?rel=0\" frameborder=\"0\" allowfullscreen></iframe>"
  end

  # soundcloud://<id>
  # soundcloud://99823063
  def soundcloud_link(link)
    resource_id = link.match(/\/\/.*?\/(.*)$/)[1]
    link = "//w.soundcloud.com/player/?url=http%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F" + resource_id
    "<iframe src=\"#{link}\" frameborder=\"0\"></iframe>"
  end

  # mixcloud://<config>
  # mixcloud://gustavo-seganfredo-farias%2Fdomingo-esporte-show-renata-medeiros-entrevista-em%25C3%25ADlio-fialho-sobre-o-consulado-virtual-gps%2F&amp;embed_uuid=4d67356e-3760-4e10-8f9e-6ad911d109eb&amp;replace=0&amp;hide_cover=1&amp;embed_type=widget_standard&amp;hide_tracklist=1
  def mixcloud_link(link)
    resource_config = link.match(/\/\/.*?\/(.*)$/)[1]
    link = "https://www.mixcloud.com/widget/iframe/?feed=http%3A%2F%2Fwww.mixcloud.com%2F" + resource_config
    "<iframe width=\"100%\" height=\"180\" src=\"#{link}\" frameborder=\"0\"></iframe>"

  end

  def normal_link(link)
    "<a href=\"#{link}\">#{link}</a>"
  end

  def email_link(email)
    "<a href=\"mailto:#{email}\">#{email}</a>"
  end

end