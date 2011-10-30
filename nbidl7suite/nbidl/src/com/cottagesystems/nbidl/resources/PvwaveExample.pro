;+
;  doc_library comment
;-
function user_add_em, arg1, arg2
    var=34    
    message, 'adding arguments', /cont
    return, arg1+arg2
end

pro user_mult_em, arg1, arg2, result
    message, "multiply arguments", /cont
    x= user_add_em( arg1, arg2 )  ; user function
    result= arg1 * arg2  ; inline comment
end
