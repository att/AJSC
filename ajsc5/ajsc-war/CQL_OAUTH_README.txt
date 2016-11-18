CREATE KEYSPACE oauth WITH replication = { 
  'class': 'SimpleStrategy', 
  'replication_factor': '3' 
};

USE oauth;

CREATE TABLE oauth_access_token ( 
  token_id text PRIMARY KEY, 
  authentication blob, 
  authentication_id text, 
  client_id text, 
  expiration timestamp, 
  refresh_token text, 
  token_obj blob, 
  user_name text 
);

CREATE INDEX oauth_access_token_authentication_id_idx ON oauth_access_token (authentication_id);

CREATE INDEX oauth_access_token_client_id_idx ON oauth_access_token (client_id);

CREATE INDEX oauth_access_token_refresh_token_idx ON oauth_access_token (refresh_token);

CREATE INDEX oauth_access_token_user_name_idx ON oauth_access_token (user_name);

CREATE TABLE oauth_client_details ( 
  client_id text PRIMARY KEY, 
  access_token_validity int, 
  additional_information text, 
  authorities text, 
  authorized_grant_types set<text>, 
  client_secret text, 
  refresh_token_validity int, 
  resource_ids text, 
  scope set<text>, 
  web_server_redirect_uri set<text> 
);

CREATE TABLE oauth_refresh_token ( 
  token_id text PRIMARY KEY, 
  authentication blob, 
  token_obj blob 
);


sample insert
insert into oauth_client_details (client_id,access_token_validity,additional_information,authorities,authorized_grant_types,client_secret,refresh_token_validity,resource_ids,scope) values('xyzCorp',30,'xyzCorp US Division','ROLE_CLIENT',{'client_credentials'},'secret',360,'ajscRest',{'read,write'});