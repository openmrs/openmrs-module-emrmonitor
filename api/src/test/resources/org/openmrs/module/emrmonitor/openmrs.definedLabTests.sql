select    cn.name
from      concept_name cn, concept c
where     cn.concept_id = c.concept_id
and       cn.concept_name_type = 'SHORT'
and       c.class_id = 1
order by  cn.name